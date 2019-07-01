package com.tifone.demo.camera.model

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.camera.CameraId
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

    private var mCameraDevice: CameraDevice? = null
    private var mContext = context
    private var mBackgroundThread: HandlerThread? = HandlerThread("camera2 thread")
    private var mBackgroundHandler: Handler
    private var mCameraManager: CameraManager
    private var mCameraAsyncRunner: CameraAsyncRunner
    private val mCameraOperationLock = Semaphore(1)
    private lateinit var mCameraStatusCallback: CameraStatusCallback
    private lateinit var mPreviewSurface: Surface
    private var mCaptureSession: CameraCaptureSession? = null

    companion object {
        private const val OPERATION_OPEN_CAMERA = 1
        private const val OPERATION_CLOSE_CAMERA = 2
        private const val OPERATION_CREATE_SESSION= 3
    }

    init {
        // 初始化线程和handle，用于在后台处理消息
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
        mCameraAsyncRunner = CameraAsyncRunner("camera2 bg thread")
        mCameraAsyncRunner.setCallback(this)
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun setCameraStatusCallback(callback: CameraStatusCallback) {
        mCameraStatusCallback = callback
    }

    override fun openCamera(cameraId: String) {
        mCameraAsyncRunner.run(OPERATION_OPEN_CAMERA, cameraId)
    }

    override fun createSession(surface: Surface) {
        mPreviewSurface = surface
        mCameraAsyncRunner.run(OPERATION_CREATE_SESSION)
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

    override fun onTaskRun(what: Int, any: Any) {
        // run on background thread
        when(what) {
            OPERATION_OPEN_CAMERA -> handleOpenCamera(any as String)
            OPERATION_CLOSE_CAMERA -> handleCloseCamera()
            OPERATION_CREATE_SESSION -> handleCreateSession()
        }
    }
    private fun releaseLock() {
        mCameraOperationLock.release()
    }
    private fun acquireLock() {
        try {
            mCameraOperationLock.acquire()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    private val mCameraOpenStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            releaseLock()
            mCameraDevice = camera
            mCameraStatusCallback.onCameraOpened()
        }

        override fun onDisconnected(camera: CameraDevice) {
            releaseLock()
            mCameraDevice = null
            mCameraStatusCallback.onCameraDisconnected()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            releaseLock()
            mCameraDevice = null
            mCameraStatusCallback.onCameraError()
        }

    }

    @SuppressLint("MissingPermission")
    private fun handleOpenCamera(cameraId: String) {
        // check camera permission, if not permission :return
        try {
            // acquire the camera open lock
            if (mCameraOperationLock.tryAcquire(2000, TimeUnit.MILLISECONDS)) {
                mCameraManager.openCamera(cameraId, mCameraOpenStateCallback, mBackgroundHandler)
            } else {
                loge("camera open timeout")
                mCameraStatusCallback.onCameraError()
            }
            // try to openCamera
        } catch (e: CameraAccessException) {
            // resolve the exception
        }

    }

    private fun handleCreateSession() {
        if (mCameraDevice == null) {
            return
        }
        val surfaces = listOf(mPreviewSurface)
        mCameraDevice?.apply {
            acquireLock()
            createCaptureSession(surfaces, mCreateSessionCallback, mBackgroundHandler)
        }
    }
    private val mCreateSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            releaseLock()
            mCaptureSession = null
            mCameraStatusCallback.onCameraError()
        }

        override fun onConfigured(session: CameraCaptureSession) {
            releaseLock()
            mCaptureSession = session
        }

    }

    private fun handleCloseCamera() {

    }

    fun loge(msg: String) {
        loge(this, msg)
    }
}