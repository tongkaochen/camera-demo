package com.tifone.demo.camera.module

import com.tifone.demo.camera.presenter.VideoPresenter
import com.tifone.demo.camera.view.CameraUI

class VideoModule(cameraUI: CameraUI) : BaseModule{

    private var mCameraUI: CameraUI? = cameraUI
    private var mPresenter: VideoPresenter? = null

    override fun create() {
        mCameraUI?.apply {
            mPresenter = VideoPresenter(this)
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
        return ModuleID.VIDEO
    }
}