package com.tifone.demo.camera.presenter

import com.tifone.demo.camera.model.BaseCameraModel
import com.tifone.demo.camera.model.CameraModelManager
import com.tifone.demo.camera.stragety.ApiLevel
import com.tifone.demo.camera.stragety.CameraApiStrategy
import com.tifone.demo.camera.view.CameraUI

/**
 * resolve the basic camera request
 */
open class CameraPresenter(cameraUI: CameraUI){
    protected var mCameraUI: CameraUI = cameraUI
    init {

    }
    fun openCamera(cameraId: String) {

    }
    fun closeCamera() {

    }
    fun applyFlashMode() {

    }

    fun applyAEMode() {

    }
    fun applyAFMode() {

    }
    fun applyZoom() {

    }
    fun getCameraModule(): BaseCameraModel {
        return CameraModelManager.getCameraModel(ApiLevel.API2)
    }
}