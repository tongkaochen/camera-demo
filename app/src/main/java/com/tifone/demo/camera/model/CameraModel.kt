package com.tifone.demo.camera.model

import android.content.Context
import java.lang.ref.WeakReference

/**
 * for camera api 1
 * it should be singleton
 */
class CameraModel(context: Context): BaseCameraModel {
    private var mContext = context

    override fun openCamera(cameraId: String) {

    }

    override fun closeCamera() {

    }

    override fun destroy() {

    }
}