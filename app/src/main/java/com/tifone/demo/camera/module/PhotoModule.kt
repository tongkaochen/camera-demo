package com.tifone.demo.camera.module

import com.tifone.demo.camera.presenter.PhotoPresenter
import com.tifone.demo.camera.view.CameraUI

class PhotoModule(cameraUI: CameraUI) : BaseModule {
    private var mCameraUI: CameraUI? = cameraUI
    private var mPresenter: PhotoPresenter? = null
    companion object {
        private var INSTANCE: PhotoModule? = null
        private val mObject = Object()
        fun getInstance(view: CameraUI): PhotoModule {
            var instance = INSTANCE
            if (instance == null) {
                synchronized(mObject) {
                    instance = INSTANCE
                    if (INSTANCE == null) {
                        instance = PhotoModule(view)
                        INSTANCE = instance
                    }
                }
            }
            return instance!!
        }
    }

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