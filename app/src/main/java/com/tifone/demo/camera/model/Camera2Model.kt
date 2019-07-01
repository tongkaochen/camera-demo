package com.tifone.demo.camera.model

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.callback.TakePictureCallback
import com.tifone.demo.camera.logd
import com.tifone.demo.camera.loge
import com.tifone.demo.camera.task.CameraAsyncRunner
import com.tifone.demo.camera.task.TaskRunner
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * camera operation for api 2
 * it should be a singleton
 */
class Camera2Model(context: Context): BaseCameraModel, TaskRunner.Callback {

    private var mPreviewRequest: CaptureRequest? = null
    private var mCameraDevice: CameraDevice? = null
    private var mContext = context
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mCameraManager: CameraManager
    private var mCameraAsyncRunner: CameraAsyncRunner = CameraAsyncRunner("camera2 bg thread")
    private val mCameraOperationLock = Semaphore(1)
    private lateinit var mCameraStatusCallback: CameraStatusCallback
    private var mPreviewSurface: Surface? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mTakePictureCallback: TakePictureCallback ? = null

    companion object {
        private const val OPERATION_OPEN_CAMERA = 1
        private const val OPERATION_CLOSE_CAMERA = 2
        private const val OPERATION_CREATE_SESSION = 3
        private const val OPERATION_TAKE_PICTURE = 3
    }

    init {
        mCameraAsyncRunner.setCallback(this)
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun setCameraStatusCallback(callback: CameraStatusCallback) {
        mCameraStatusCallback = callback
    }
    private fun startBackgroundThread() {
        // 初始化线程和handle，用于在后台处理消息
        mBackgroundThread = HandlerThread("camera2 thread")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }
    private fun stopBackgroundThread() {
        mBackgroundThread?.apply {
            quitSafely()
            join()
            mBackgroundThread = null
            mBackgroundHandler = null
        }

    }

    override fun openCamera(cameraId: String) {
        mCameraAsyncRunner.run(OPERATION_OPEN_CAMERA, cameraId)
    }

    override fun startPreview(surface: Surface) {
        if (mCameraDevice == null) {
            loge("camera is not start, could not start preview, return")
            return
        }
        mPreviewSurface = surface
        mCameraAsyncRunner.run(OPERATION_CREATE_SESSION)
    }

    override fun setTakePictureCallback(callback: TakePictureCallback) {
        mTakePictureCallback = callback
    }
    override fun takePicture() {
        if (mCameraDevice == null ||
                mCaptureSession == null) {
            loge("take picture failed")
            return
        }
        mCameraAsyncRunner.run(OPERATION_TAKE_PICTURE)
    }

    override fun closeCamera() {
        mCameraAsyncRunner.run(OPERATION_CLOSE_CAMERA)
    }

    override fun destroy() {
        mBackgroundThread?.apply {
            quitSafely()
            mBackgroundThread = null
        }
    }

    override fun onTaskRun(what: Int, any: Any?) {
        // run on background thread
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
            // try to openCamera
            // acquire the camera open lock
            if (mCameraOperationLock.tryAcquire(2000, TimeUnit.MILLISECONDS)) {
                mCameraManager.openCamera(cameraId, mCameraOpenStateCallback, mBackgroundHandler)
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
        val surfaces = listOf(mPreviewSurface)
        mCameraDevice?.apply {
            acquireCameraOperationLock()
            createCaptureSession(surfaces, mCreateSessionCallback, mBackgroundHandler)
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

        mPreviewRequest = previewBuilder.build()
        // start preview
        mCaptureSession!!.setRepeatingRequest(mPreviewRequest,
                mPreviewCaptureCallback, mBackgroundHandler)
    }
    private val mPreviewCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
            //logd("onCaptureCompleted")
        }

        override fun onCaptureFailed(session: CameraCaptureSession,
                                     request: CaptureRequest,
                                     failure: CaptureFailure) {
            super.onCaptureFailed(session, request, failure)
            logd("onCaptureFailed")
        }

        override fun onCaptureProgressed(session: CameraCaptureSession?,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            super.onCaptureProgressed(session, request, partialResult)
            //logd("onCaptureProgressed")
        }

        override fun onCaptureBufferLost(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         target: Surface, frameNumber: Long) {
            super.onCaptureBufferLost(session, request, target, frameNumber)
            logd("onCaptureBufferLost")
        }
    }

    private fun handleTakePicture() {
        if (!lockFocus()) {
            loge("lock focus failed")
            mTakePictureCallback?.onTakeFail("lock focus failed")
        }
    }
    private fun lockFocus(): Boolean {
        if (mCameraDevice == null || mCaptureSession == null) {
            loge("could lock focus")
            return false
        }
        // create the capture request
        val builder = getCaptureRequestBuilder() ?: return false

        builder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_START)
        // apply AE/AF value
        try {
            // start capture
            mCaptureSession!!.capture(builder.build(),
                    mPreviewCaptureCallback, mBackgroundHandler)
            return true
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return false
    }
    private fun getCaptureRequestBuilder(): CaptureRequest.Builder? {
        return try {
            var builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
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
        logd(this, msg)
    }
    fun loge(msg: String) {
        loge(this, msg)
    }
}