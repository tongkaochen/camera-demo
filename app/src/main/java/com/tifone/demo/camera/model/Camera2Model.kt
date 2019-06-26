package com.tifone.demo.camera.model

import android.content.Context
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.HandlerThread
import com.tifone.demo.camera.task.CameraAsyncRunner
import com.tifone.demo.camera.task.TaskRunner

/**
 * camera operation for api 2
 * it should be a singleton
 */
class Camera2Model(context: Context): BaseCameraModel, TaskRunner.Callback {

    private var mContext = context
    private var mBackgroundThread: HandlerThread? = HandlerThread("camera2 thread")
    private var mBackgroundHandler: Handler
    private var mCameraManager: CameraManager
    private var mCameraAsyncRunner: CameraAsyncRunner

    companion object {
        private const val OPERATION_OPEN_CAMERA = 1
        private const val OPERATION_CLOSE_CAMERA = 2
        private const val OPERATION_START_PREVIEW = 3
    }

    init {
        // 初始化线程和handle，用于在后台处理消息
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
        mCameraAsyncRunner = CameraAsyncRunner("camera2 bg thread")
        mCameraAsyncRunner.setCallback(this)
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun openCamera(cameraId: String) {
        mCameraAsyncRunner.run(OPERATION_OPEN_CAMERA, cameraId)
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
        }
    }

    private fun handleOpenCamera(cameraId: String) {

    }
    private fun handleCloseCamera() {

    }


}