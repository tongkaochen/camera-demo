package com.tifone.demo.camera.module

import com.tifone.demo.camera.presenter.VideoPresenter
import com.tifone.demo.camera.view.CameraUI

class VideoModule(cameraUI: CameraUI) : BaseModule{

    private var mCameraUI: CameraUI? = cameraUI
    private var mPresenter: VideoPresenter? = null

    companion object {
        private var INSTANCE: VideoModule? = null
        private val mObject = Object()
        fun getInstance(view: CameraUI): VideoModule {
            var instance = INSTANCE
            if (instance == null) {
                synchronized(mObject) {
                    instance = INSTANCE
                    if (INSTANCE == null) {
                        instance = VideoModule(view)
                        INSTANCE = instance
                    }
                }
            }
            return instance!!
        }
    }

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