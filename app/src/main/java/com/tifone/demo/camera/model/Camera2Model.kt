package com.tifone.demo.camera.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.callback.TakePictureCallback
import com.tifone.demo.camera.camera.CameraInfo
import com.tifone.demo.camera.camera.CameraSettings
import com.tifone.demo.camera.device.DeviceInfo
import com.tifone.demo.camera.tlogd
import com.tifone.demo.camera.tloge
import com.tifone.demo.camera.task.CameraAsyncRunner
import com.tifone.demo.camera.task.TaskRunner
import com.tifone.demo.camera.utils.CameraUtil
import com.tifone.demo.camera.utils.ImageUtil
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * camera operation for api 2
 * it should be a singleton
 */
class Camera2Model(context: Context): BaseCameraModel, TaskRunner.Callback {

    private var mCaptureHandler: Handler? = null
    private var mCaptureThread: HandlerThread? = null
    private var mPreviewRequest: CaptureRequest? = null
    private var mCameraDevice: CameraDevice? = null
    private var mContext = context
    private var mCameraPreviewThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mCameraManager: CameraManager
    private var mCameraAsyncRunner: CameraAsyncRunner =
            CameraAsyncRunner("camera2 operations bg thread")
    private val mCameraOperationLock = Semaphore(1)
    private lateinit var mCameraStatusCallback: CameraStatusCallback
    private var mPreviewSurface: Surface? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mTakePictureCallback: TakePictureCallback ? = null
    private lateinit var mCameraInfo: CameraInfo
    private lateinit var mCaptureImageReader: ImageReader
    private var mCaptureBuilder: CaptureRequest.Builder? = null
    private var mPreviewBuilder: CaptureRequest.Builder? = null
    private var mOrientationHelper: CameraOrientationHelper = CameraOrientationHelper()
    private var mState = State.PREVIEW

    // the state of current camera, use to take picture
    private enum class State {
        PREVIEW,
        WAITING_AF_LOCKED,
        WAITING_PRE_CAPTURE,
        WAITING_AE_LOCKED,
        PICTURE_TAKEN
    }
    // camera operations
    companion object {
        // request to open camera
        private const val OPERATION_OPEN_CAMERA = 1
        // request to close camera
        private const val OPERATION_CLOSE_CAMERA = 2
        // request to create session
        private const val OPERATION_CREATE_SESSION = 3
        // request to take picture
        private const val OPERATION_TAKE_PICTURE = 4
    }

    init {
        // all the camera operation run in CameraAsyncRunner thread
        mCameraAsyncRunner.setCallback(this)
        mCameraManager = mContext.getSystemService(
                Context.CAMERA_SERVICE) as CameraManager
    }

    override fun setCameraStatusCallback(callback: CameraStatusCallback) {
        // notify client when camera status changed
        mCameraStatusCallback = callback
    }
    private fun startBackgroundThread() {
        // 初始化线程和handle，用于在后台处理消息
        mCameraPreviewThread = HandlerThread("camera2 preview thread")
        mCameraPreviewThread!!.start()
        mBackgroundHandler = Handler(mCameraPreviewThread!!.looper)

        mCaptureThread = HandlerThread("image capture thread")
        mCaptureThread!!.start()
        mCaptureHandler = Handler(mCaptureThread!!.looper)
    }
    private fun stopBackgroundThread() {
        mCameraPreviewThread?.apply {
            quitSafely()
            join()
            mCameraPreviewThread = null
            mBackgroundHandler = null
        }
        mCaptureThread?.apply {
            quitSafely()
            join()
            mCaptureThread = null
            mCaptureHandler = null
        }
    }

    override fun openCameraAsync(cameraInfo: CameraInfo) {
        mCameraInfo = cameraInfo
        // send open camera event to async runner
        mCameraAsyncRunner.run(OPERATION_OPEN_CAMERA, cameraInfo.getCameraId())
    }

    override fun startPreviewAsync(surface: Surface) {
        if (mCameraDevice == null) {
            loge("camera is not start, could not start preview, return")
            return
        }
        mPreviewSurface = surface
        mCameraAsyncRunner.run(OPERATION_CREATE_SESSION)
    }

    override fun setTakePictureCallback(callback: TakePictureCallback) {
        // notify client when take picture is finished
        mTakePictureCallback = callback
    }
    override fun takePictureAsync() {
        if (mCameraDevice == null ||
                mCaptureSession == null) {
            loge("take picture failed")
            return
        }
        mCameraAsyncRunner.run(OPERATION_TAKE_PICTURE)
    }

    override fun closeCameraAsync() {
        mCameraAsyncRunner.run(OPERATION_CLOSE_CAMERA)
    }

    override fun destroy() {
        mCameraAsyncRunner.quit()
    }

    // handle camera operation
    override fun onTaskRun(what: Int, any: Any?) {
        // run on background thread
        logd("what = $what")
        when(what) {
            OPERATION_OPEN_CAMERA -> handleOpenCamera(any as String)
            OPERATION_CLOSE_CAMERA -> handleCloseCamera()
            OPERATION_CREATE_SESSION -> handleCreateSession()
            OPERATION_TAKE_PICTURE -> handleTakePicture()
        }
    }

    private fun releaseCameraOperationLock() {
        mCameraOperationLock.release()
    }

    private fun acquireCameraOperationLock() {
        try {
            mCameraOperationLock.acquire()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            releaseCameraOperationLock()
        }
    }
    private val mCameraOpenStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            logd("onOpened")
            releaseCameraOperationLock()
            mCameraDevice = camera
            mCameraStatusCallback.onCameraOpened()
        }

        override fun onDisconnected(camera: CameraDevice) {
            logd("onDisconnected")
            releaseCameraOperationLock()
            mCameraDevice = null
            mCameraStatusCallback.onCameraDisconnected()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            logd("onError")
            releaseCameraOperationLock()
            mCameraDevice = null
            mCameraStatusCallback.onCameraError()
        }

    }

    @SuppressLint("MissingPermission")
    private fun handleOpenCamera(cameraId: String) {
        logd("handleOpenCamera")
        startBackgroundThread()
        // check camera permission, if not permission :return
        try {
            // try to openCameraAsync
            // acquire the camera open lock
            if (mCameraOperationLock.tryAcquire(2000, TimeUnit.MILLISECONDS)) {
                mCameraManager.openCamera(cameraId, mCameraOpenStateCallback,
                        mBackgroundHandler)
            } else {
                loge("camera open timeout")
                mCameraStatusCallback.onCameraError()
            }

        } catch (e: CameraAccessException) {
            // resolve the exception
            e.printStackTrace()
            mCameraStatusCallback.onCameraError()
            releaseCameraOperationLock()
        } catch (e: SecurityException) {
            //
            e.printStackTrace()
            mCameraStatusCallback.onCameraError()
            releaseCameraOperationLock()
        }

    }

    private fun handleCreateSession() {
        if (mCameraDevice == null || mPreviewSurface == null) {
            loge("could create session, due to  camera device: $mCameraDevice," +
                    " preview surface: $mPreviewSurface")
            return
        }
        initImageReader()
        val surfaces = listOf(mPreviewSurface, mCaptureImageReader.surface)
        mCameraDevice?.apply {
            acquireCameraOperationLock()
            createCaptureSession(surfaces, mCreateSessionCallback, mBackgroundHandler)
        }
    }
    private fun initImageReader() {
        val previewSize = mCameraInfo.getCurrentPreviewSize()
        // specify the aspect ratio to capture, default is 4:3
        val ratio = previewSize?.let {
            CameraUtil.getAspectRatio(it)
        }?:CameraSettings.ASPECT_RATIO_4_3
        val captureSize = mCameraInfo.getOutputImageSize(ImageFormat.JPEG, ratio)
        mCaptureImageReader = ImageReader.newInstance(
                captureSize.width, captureSize.height, ImageFormat.JPEG, 1)
        mCaptureImageReader.setOnImageAvailableListener(
                mOnImageAvailableListener, mCaptureHandler)
    }
    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        logd("mOnImageAvailableListener")
        val image = reader.acquireLatestImage()
        val bytes = ImageUtil.imageToBytes(image)
        image.close()
        mCaptureHandler?.post {
            mTakePictureCallback?.onTakenComplete(bytes)
        }
    }
    private val mCreateSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            releaseCameraOperationLock()
            mCaptureSession = null
            mCameraStatusCallback.onCameraError()
        }

        override fun onConfigured(session: CameraCaptureSession) {
            // camera have configured, can start repeating to preview now
            releaseCameraOperationLock()
            mCaptureSession = session
            startRepeat()
        }

    }
    private fun startRepeat() {
        // check the decision
        if ((mCaptureSession == null) or
                (mCameraDevice == null)) {
            // could not start preview
            loge("start preview failed")
            return
        }
        // create preview request
        val previewBuilder = mCameraDevice!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW)
        // add surface to request
        previewBuilder.addTarget(mPreviewSurface)
        // apply some settings to request
        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        mPreviewBuilder = previewBuilder
        mPreviewRequest = previewBuilder.build()
        // start preview
        mCaptureSession!!.setRepeatingRequest(mPreviewRequest,
                mPreviewCaptureCallback, mBackgroundHandler)
    }
    private val mPreviewCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        private fun run(result: CaptureResult) {
            when(mState) {
                State.WAITING_AF_LOCKED -> onWaitingAFLocked(result)
                State.WAITING_PRE_CAPTURE -> onPreCapture(result)
                State.WAITING_AE_LOCKED -> onWaitingAELocked(result)
            }
        }
        private fun onWaitingAELocked(result: CaptureResult) {
            val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
            logd("onWaitingAELocked, aeState:$aeState")
            if (aeState == null
                    || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                mState = State.PICTURE_TAKEN
                captureStillPicture()
            }
        }

        private fun onPreCapture(result: CaptureResult) {
            val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
            logd("onPreCapture: aeState:$aeState")
            if (aeState == null
                    || aeState == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED
                    || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE
                    || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                mState = State.WAITING_AE_LOCKED
            }
        }

        private fun onWaitingAFLocked(result: CaptureResult) {
            val afState = result.get(CaptureResult.CONTROL_AF_STATE)
            logd("afState = $afState")
            if (afState == null) {
                mState = State.PICTURE_TAKEN
                captureStillPicture()
            } else if (afState == CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED
                    || afState == CaptureRequest.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                logd("aeState = $aeState")
                if (aeState == null || aeState == CaptureRequest.CONTROL_AE_STATE_CONVERGED) {
                    mState = State.PICTURE_TAKEN
                    captureStillPicture()
                } else {
                    runPreCaptureSequence()
                }
            }
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            //tlogd("onCaptureCompleted")
            run(result)
        }

        override fun onCaptureFailed(session: CameraCaptureSession,
                                     request: CaptureRequest,
                                     failure: CaptureFailure) {
            logd("onCaptureFailed")
        }

        override fun onCaptureProgressed(session: CameraCaptureSession?,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            //tlogd("onCaptureProgressed")
            run(partialResult)
        }

        override fun onCaptureBufferLost(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         target: Surface, frameNumber: Long) {
            logd("onCaptureBufferLost")
        }
    }

    private fun runPreCaptureSequence() {
        logd("runPreCaptureSequence")
        if (mCaptureBuilder == null) {
            mCaptureBuilder = getCaptureRequestBuilder()
        }
        mCaptureBuilder?.also {
            it.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            mState = State.WAITING_PRE_CAPTURE
            mCaptureSession?.capture(it.build(), mPreviewCaptureCallback, mCaptureHandler)
        }
    }

    private fun handleTakePicture() {
        if (!lockFocus()) {
            loge("lock focus failed")
            mTakePictureCallback?.onTakeFailed("lock focus failed")
        }
    }
    private fun lockFocus(): Boolean {
        if (mCameraDevice == null || mCaptureSession == null) {
            loge("could lock focus")
            return false
        }
        // create the capture request
        val builder = getCaptureRequestBuilder() ?: return false

        // request trigger AF
        builder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_START)
        // apply AE/AF value
        try {
            mCaptureBuilder = builder
            mState = State.WAITING_AF_LOCKED
            // start capture
            mCaptureSession!!.capture(builder.build(),
                    mPreviewCaptureCallback, mBackgroundHandler)
            return true
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return false
    }
    private fun captureStillPicture() {
        logd("captureStillPicture")
        // create request build
        val captureBuilder = mCameraDevice?.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE)
                ?: return mTakePictureCallback?.onTakeFailed("camera device is null")!!
        // apply AE/ Orientation settings to request
        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        val deviceRotation = DeviceInfo.get().getRotation()
        val sensorRotation = mCameraInfo.getSensorOrientation()
        val pictureRotation = mOrientationHelper.getOrientation(deviceRotation, sensorRotation)
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, pictureRotation)
        // add capture surface
        captureBuilder.addTarget(mCaptureImageReader.surface)
        // stop current repeat
        mCaptureSession?.stopRepeating() ?: return mTakePictureCallback?.onTakeFailed("mCaptureSession is null")!!

        val captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(session: CameraCaptureSession,
                                            request: CaptureRequest,
                                            result: TotalCaptureResult) {
                // capture complete, unlock focus
                unlockFocus()
            }

            override fun onCaptureFailed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         failure: CaptureFailure) {
                unlockFocus()
                mTakePictureCallback?.onTakeFailed("capture fail")
            }
        }
        // capture image
        mCaptureSession!!.capture(captureBuilder.build(), captureCallback, mCaptureHandler)
    }

    private fun unlockFocus() {
        logd("unlockFocus")
        mCaptureBuilder?.apply {
            mCaptureBuilder = getCaptureRequestBuilder()
        }
        try {
            mCaptureBuilder!!.apply {
                set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL)
                set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                        CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL)
            }
            mCaptureSession?.capture(mCaptureBuilder!!.build(), null, mCaptureHandler) ?: return
            mCaptureSession!!.setRepeatingRequest(mPreviewRequest, mPreviewCaptureCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun getCaptureRequestBuilder(): CaptureRequest.Builder? {
        return try {
            var builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            builder.addTarget(mPreviewSurface)
            builder
        }catch (e: CameraAccessException) {
            e.printStackTrace()
            null
        }
    }

    private fun handleCloseCamera() {
        // close camera and release resources
        acquireCameraOperationLock()
        mCaptureSession?.apply {
            // stop capture session
            stopRepeating()
            mCaptureSession = null
            mPreviewRequest = null

        }
        mPreviewSurface?.apply {
            // release preview surface
            release()
            mPreviewSurface = null
        }
        mCameraDevice?.apply {
            // close camera device
            close()
            mCameraDevice = null
        }
        releaseCameraOperationLock()
        stopBackgroundThread()
    }

    fun logd(msg: String) {
        tlogd(this, msg)
    }
    fun loge(msg: String) {
        tloge(this, msg)
    }
}