package com.tifone.demo.camera.presenter

import com.tifone.demo.camera.logd
import com.tifone.demo.camera.model.BaseCameraModel
import com.tifone.demo.camera.model.CameraModelManager
import com.tifone.demo.camera.stragety.ApiLevel
import com.tifone.demo.camera.view.CameraUI

/**
 * resolve the basic camera request
 */
open abstract class CameraPresenter(cameraUI: CameraUI){
    protected var mCameraUI: CameraUI = cameraUI
    protected var mCameraModel: BaseCameraModel? = null
    init {
        logd("init")
        createCameraModule()
    }
    fun openCamera(cameraId: String) {
        logd("openCamera")
    }
    fun closeCamera() {
        logd("closeCamera")
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
        }
    }
    private fun logd(msg: String) {
        logd(this, msg)
    }
}