package com.tifone.demo.camera.model

/**
 * for camera api 1
 * it should be singleton
 */
class CameraModel private constructor(): BaseCameraModel {

    companion object {
        private var INSTANCE: CameraModel? = null
            get() {
                // attribute accessor
                if (field == null) {
                    field = CameraModel()
                }
                return field
            }
        @Synchronized
        fun get(): CameraModel {
            return INSTANCE!!
        }
    }

    override fun openCamera(cameraId: String) {

    }

    override fun closeCamera() {

    }
}