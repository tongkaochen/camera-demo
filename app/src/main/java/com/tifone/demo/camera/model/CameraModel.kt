package com.tifone.demo.camera.model

import android.content.Context
import android.view.Surface
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.callback.TakePictureCallback
import com.tifone.demo.camera.camera.CameraInfo
import java.lang.ref.WeakReference

/**
 * for camera api 1
 * it should be singleton
 */
class CameraModel(context: Context): BaseCameraModel {
    override fun setCameraStatusCallback(callback: CameraStatusCallback) {

    }

    override fun startPreviewAsync(surface: Surface) {
    }

    private var mContext = context

    override fun openCameraAsync(cameraInfo: CameraInfo) {

    }

    override fun takePictureAsync() {
    }

    override fun setTakePictureCallback(callback: TakePictureCallback) {
    }

    override fun closeCameraAsync() {

    }

    override fun destroy() {

    }
}