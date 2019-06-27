package com.tifone.demo.camera.module

import com.tifone.demo.camera.presenter.PhotoPresenter
import com.tifone.demo.camera.view.CameraUI

/**
 * camera photo module
 * manage the photo relate logic
 */
class PhotoModule(cameraUI: CameraUI) : BaseModule {
    private var mCameraUI: CameraUI? = cameraUI
    private var mPresenter: PhotoPresenter? = null

    override fun create() {
        mCameraUI?.apply {
            mPresenter = PhotoPresenter(this)
        }
    }

    override fun start() {
        // TODO
    }

    override fun pause() {
        // TODO
    }

    override fun destroy() {
        // TODO
        mCameraUI = null
    }

    override fun getId(): ModuleID {
        return ModuleID.PHOTO
    }


}