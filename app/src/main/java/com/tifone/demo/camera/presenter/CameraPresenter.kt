package com.tifone.demo.camera.presenter

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.callback.TakePictureCallback
import com.tifone.demo.camera.camera.CameraId
import com.tifone.demo.camera.camera.CameraInfo
import com.tifone.demo.camera.logd
import com.tifone.demo.camera.loge
import com.tifone.demo.camera.logw
import com.tifone.demo.camera.model.BaseCameraModel
import com.tifone.demo.camera.model.CameraModelManager
import com.tifone.demo.camera.preview.PreviewSizeHelper
import com.tifone.demo.camera.stragety.ApiLevel
import com.tifone.demo.camera.utils.PermissionUtil
import com.tifone.demo.camera.view.CameraUI
import com.tifone.demo.camera.view.ViewState
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * resolve the basic camera request
 */
open abstract class CameraPresenter(cameraUI: CameraUI) {
    private object CONSTANT {
        const val SURFACE_AVAILABLE_LOCK_TIMEOUT:Long = 2000
    }
    protected var mCameraUI: CameraUI = cameraUI
    protected lateinit var mCameraModel: BaseCameraModel
    protected var mSurfaceTexture: SurfaceTexture? = null
    protected var mPreviewSize: Size? = null
    private var mSurfacePrepared = false
    private val mSurfacePreparedLock = Semaphore(1)
    private var mCameraOpened = false
    private var mCameraInfo: CameraInfo =  CameraInfo(mCameraUI.getContext())

    fun init() {
        logd("init")
        createCameraModule()
    }
    private fun isCameraOpenAllowed(): Boolean {
        if (!PermissionUtil.isCameraPermissionGranted(mCameraUI.getContext())) {
            loge("camera permission is not granted, return")
            return false
        }
        if (mCameraOpened) {
            logw("camera is opened")
            return false
        }
        return true
    }
    fun openCamera(cameraId: CameraId) {
        logd("openCamera")
        if (!isCameraOpenAllowed()) {
            return
        }
        mCameraInfo.setCameraId(cameraId)
        mPreviewSize = mCameraInfo.getPreviewSize(
                SurfaceTexture::class.java, mCameraUI.getUIAspectRatio())
        logd("mPreviewSize: $mPreviewSize")
        mCameraModel.openCamera(mCameraInfo)
        mCameraOpened = true
    }
    private val mCameraStatusCallback =
            object : CameraStatusCallback {
                override fun onCameraOpened() {
                    mCameraOpened = true
                    tryToStartPreview()
                }

                override fun onCameraDisconnected() {
                    mCameraOpened = false
                }

                override fun onCameraClosed() {
                    mCameraOpened = false
                }

                override fun onCameraError() {
                    mCameraOpened = false
                }
            }
    private fun tryToStartPreview() {
        if (mSurfacePrepared) {
            logd("startPreview")
            mSurfaceTexture?.apply {
                setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
            }
            mCameraModel.startPreview(Surface(mSurfaceTexture))
        } else {
            waitingForSurfaceAvailable()
        }
    }
    private fun waitingForSurfaceAvailable() {
        if (!mSurfacePreparedLock.tryAcquire(
                        CONSTANT.SURFACE_AVAILABLE_LOCK_TIMEOUT,
                        TimeUnit.MILLISECONDS)) {
            if (mCameraUI.getViewState() != ViewState.DISPLAYING) {
                loge(this, "mPaused status occur Time out waiting for surface.")
                //throw IllegalStateException("Paused Time out waiting for surface.")
            } else {
                loge(this, "Time out waiting for surface.")
                //throw RuntimeException("Time out waiting for surface.")
            }
        } else {
            logd("surface ready to preview")
            tryToStartPreview()
        }
        mSurfacePreparedLock.release()
    }
    fun closeCamera() {
        logd("closeCamera")
        mCameraOpened = false
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
        mCameraModel = CameraModelManager.createCameraModel(
                mCameraUI.getContext(), ApiLevel.API2)
        mCameraModel.setCameraStatusCallback(mCameraStatusCallback)
    }
    private fun logd(msg: String) {
        logd(this, msg)
    }
    private fun loge(msg: String) {
        loge(this, msg)
    }
    private fun logw(msg: String) {
        logw(this, msg)
    }

    fun onSurfaceAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurfaceTexture = surface
        mSurfacePrepared = true
        mSurfacePreparedLock.release()
    }
    fun onSurfaceDestroy() {
        mSurfacePrepared = false
        mSurfaceTexture = null
    }

    fun takePicture() {
        // process take picture
        if (mCameraOpened) {
            mCameraModel.setTakePictureCallback(mTakePictureCallback)
            mCameraModel.takePicture()
        }
    }
    private val mTakePictureCallback = object : TakePictureCallback {
        override fun onTakeComplete(data: ByteArray) {
        }

        override fun onTakeFail(msg: String) {
        }

    }
}