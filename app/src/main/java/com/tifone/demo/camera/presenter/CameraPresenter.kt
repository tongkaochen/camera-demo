package com.tifone.demo.camera.presenter

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.camera.CameraId
import com.tifone.demo.camera.camera.CameraInfo
import com.tifone.demo.camera.logd
import com.tifone.demo.camera.loge
import com.tifone.demo.camera.model.BaseCameraModel
import com.tifone.demo.camera.model.CameraModelManager
import com.tifone.demo.camera.preview.PreviewSizeHelper
import com.tifone.demo.camera.stragety.ApiLevel
import com.tifone.demo.camera.view.CameraUI
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * resolve the basic camera request
 */
open abstract class CameraPresenter(cameraUI: CameraUI){
    private object CONSTANT {
        const val SURFACE_AVAILABLE_LOCK_TIMEOUT:Long = 2000
    }
    protected var mCameraUI: CameraUI = cameraUI
    protected lateinit var mCameraModel: BaseCameraModel
    protected var mSurfaceTexture: SurfaceTexture? = null
    protected var mPreviewSize: Size? = null
    private var mSurfacePrepared = false
    private val mSurfacePreparedLock = Semaphore(1)
    init {
        logd("init")
        createCameraModule()
    }
    fun openCamera(cameraId: CameraId) {
        logd("openCamera")
        val cameraInfo = CameraInfo(mCameraUI.getContext(), cameraId)
        val availablePreviewSize = cameraInfo.getAvailablePreviewSize(SurfaceTexture::class.java)
        availablePreviewSize?.apply {
             mPreviewSize = PreviewSizeHelper().getMatchSize(
                    cameraInfo.getSensorOrientation(), availablePreviewSize)
        } ?: return

        mCameraModel.openCamera(cameraId.value())
    }
    private val mCameraStatusCallback =
            object : CameraStatusCallback {
                override fun onCameraOpened() {
                    tryToStartPreview()
                }

                override fun onCameraDisconnected() {
                }

                override fun onCameraClosed() {
                }

                override fun onCameraError() {

                }
            }
    private fun tryToStartPreview() {
        if (mSurfacePrepared) {
            mCameraModel.createSession(Surface(mSurfaceTexture))
        } else {
            if (!mSurfacePreparedLock.tryAcquire(
                            CONSTANT.SURFACE_AVAILABLE_LOCK_TIMEOUT,
                            TimeUnit.MILLISECONDS)) {
                if (true) {
                    loge(this, "mPaused status occur Time out waiting for surface.")
                    throw IllegalStateException("Paused Time out waiting for surface.")
                } else {
                    loge(this, "Time out waiting for surface.")
                    throw RuntimeException("Time out waiting for surface.")
                }
            }
            mSurfacePreparedLock.release()
        }
    }
    fun closeCamera() {
        logd("closeCamera")
        mCameraModel.closeCamera()
    }
    fun applyFlashMode() {
        logd("applyFlashMode")
    }

    fun applyAEMode() {
        logd("applyAEMode")
    }
    fun applyAFMode() {
        logd("closeCamera")
    }
    fun applyZoom() {
        logd("applyZoom")
    }
    open fun createCameraModule() {
        logd("createCameraModule")

        if (mCameraModel == null) {
            mCameraModel = CameraModelManager.createCameraModel(
                   mCameraUI.getContext(), ApiLevel.API2)
            mCameraModel.setCameraStatusCallback(mCameraStatusCallback)
        }
    }
    private fun logd(msg: String) {
        logd(this, msg)
    }

    fun onSurfaceAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurfaceTexture = surface
        mSurfacePrepared = true
        mPreviewSize?.apply {
            mSurfaceTexture!!.setDefaultBufferSize(width, height)
        }
    }
    fun onSurfaceDestroy() {
        mSurfacePrepared = false
    }
}