package com.tifone.demo.camera.model

import android.content.Context
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.callback.TakePictureCallback
import java.lang.ref.WeakReference

/**
 * for camera api 1
 * it should be singleton
 */
class CameraModel(context: Context): BaseCameraModel {
    override fun setCameraStatusCallback(callback: CameraStatusCallback) {

    }

    override fun startPreview(surface: Surface) {
    }

    private var mContext = context

    override fun openCamera(cameraId: String) {

    }

    override fun takePicture() {
    }

    override fun setTakePictureCallback(callback: TakePictureCallback) {
    }

    override fun closeCamera() {

    }

    override fun destroy() {

    }
}