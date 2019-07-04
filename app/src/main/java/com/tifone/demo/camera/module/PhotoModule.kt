package com.tifone.demo.camera.module

import android.graphics.SurfaceTexture
import android.view.View
import com.tifone.demo.camera.camera.CameraId
import com.tifone.demo.camera.event.ShutterClickDispatcher
import com.tifone.demo.camera.logd
import com.tifone.demo.camera.presenter.PhotoPresenter
import com.tifone.demo.camera.preview.TextureViewHolder
import com.tifone.demo.camera.view.CameraUI

/**
 * camera photo module
 * manage the photo relate logic
 */
class PhotoModule(cameraUI: CameraUI) : BaseModule, TextureViewHolder.SurfaceCallback {
    private var mCameraUI: CameraUI = cameraUI
    private lateinit var mPresenter: PhotoPresenter
    private val mSurfaceHolder = cameraUI.getPreviewSurfaceHolder()
    private var mShutterClickDispatcher: ShutterClickDispatcher = ShutterClickDispatcher.getDefault()

    override fun create() {
        logd("create")
        mPresenter = PhotoPresenter(mCameraUI)
        mPresenter.init()
        mSurfaceHolder.registerSurfaceCallback(this)
        mShutterClickDispatcher.register(mShutterClicked)
    }
    private val mShutterClicked = object : ShutterClickDispatcher.ShutterClickListener {
        override fun onShutterClicked(view: View) {
            takePicture()
        }
    }
    private fun takePicture() {
        logd("takePicture")
        mPresenter.takePicture()
    }

    override fun start() {
        logd("start")
        mPresenter.openCamera(CameraId.ID_BACK)
    }

    override fun stop() {
        logd("stop")
        mPresenter.closeCamera()
    }

    override fun destroy() {
        logd("destroy")
        mSurfaceHolder.unregisterSurfaceCallback(this)
        mPresenter.destroy()
    }

    override fun getId(): ModuleID {
        return ModuleID.PHOTO
    }

    override fun onSurfaceAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        logd("onSurfaceAvailable")
        mPresenter.onSurfaceAvailable(surface, width, height)
    }

    override fun onSurfaceDestroy() {
        logd("onSurfaceAvailable")
        mPresenter.onSurfaceDestroy()
    }

    override fun onSurfaceChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    private fun logd(msg: String) {
        logd(this,msg)
    }

}