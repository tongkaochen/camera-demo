package com.tifone.demo.camera.module

import android.graphics.SurfaceTexture
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

    override fun create() {
        mPresenter = PhotoPresenter(mCameraUI)
        mSurfaceHolder.registerSurfaceCallback(this)
    }

    override fun start() {
        // TODO
    }

    override fun pause() {
        // TODO
    }

    override fun destroy() {
        // TODO
        mSurfaceHolder.registerSurfaceCallback(this)
    }

    override fun getId(): ModuleID {
        return ModuleID.PHOTO
    }

    override fun onSurfaceAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mPresenter.onSurfaceAvailable(surface, width, height)
    }

    override fun onSurfaceDestroy() {
        mPresenter.onSurfaceDestroy()
    }

    override fun onSurfaceChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

}