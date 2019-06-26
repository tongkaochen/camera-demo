package com.tifone.demo.camera.agent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraDevice.StateCallback
import android.os.Handler
import android.view.Surface
import com.tifone.demo.camera.logd
import com.tifone.demo.camera.utils.CameraUtil
import com.tifone.demo.camera.utils.PermissionUtil

class CameraOperationsImpl :
        CameraOperations<SurfaceTexture>,
        StateCallback {
    private var mContext: Context
    private var mCameraManager: CameraManager
    private var mHandler: Handler
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureRequestBuilder: CaptureRequest.Builder? = null
    private lateinit var mSurface: SurfaceTexture
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequest: CaptureRequest? = null

    constructor(context: Context) {
        mContext = context
        mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mHandler = Handler()
    }

    override fun open(surface: SurfaceTexture) {
        mSurface = surface
        openCameraAndCheckPermission()
    }

    @SuppressLint("MissingPermission")
    private fun openCameraAndCheckPermission() {
        if (!PermissionUtil.isCameraPermissionGranted(mContext)) {
            return
        }
        CameraUtil.requestBackCamera()
        mCameraManager.openCamera(CameraUtil.cameraId, this, mHandler)
    }

    override fun startPreview() {
    }


    override fun onOpened(camera: CameraDevice) {
        // camera is opened
        mCameraDevice = camera
        createPreviewSession()
    }

    override fun onDisconnected(camera: CameraDevice) {
        camera.close()
        mCameraDevice = null
    }

    override fun onError(camera: CameraDevice, error: Int) {
        camera.close()
        mCameraDevice = null
    }

    private fun createPreviewSession() {
        mCaptureRequestBuilder = mCameraDevice
                ?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        //mSurface.setDefaultBufferSize(640, 480)
        val surface = Surface(mSurface)
        mCaptureRequestBuilder?.addTarget(surface)
        mCameraDevice?.createCaptureSession(
                listOf(surface), mCameraStateCallback, mHandler)
    }

    private val mCameraStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            logd("onConfigureFailed")
            mCaptureSession = null
        }

        override fun onConfigured(session: CameraCaptureSession) {
            logd("onConfigured")
            mCaptureSession = session
            setupCameraConfigure()
        }
    }
    private fun setupCameraConfigure() {
        mCameraDevice?:return
        mCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        mCaptureRequestBuilder?.set(CaptureRequest.JPEG_ORIENTATION, 270)
        CameraCharacteristics.SENSOR_ORIENTATION
        val characteristics = mCameraManager.getCameraCharacteristics(CameraUtil.cameraId);
        logd("orientation = ${characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)}")
        mPreviewRequest = mCaptureRequestBuilder?.build()
        mCaptureSession?.setRepeatingRequest(mPreviewRequest, mCameraCaptureCallback, mHandler)
        logd("setup camera configure")
    }
    private val mCameraCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

    }
}