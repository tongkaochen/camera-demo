package com.tifone.demo.camera.model

/**
 * camera operation for api 2
 * it should be a singleton
 */
class Camera2Model private constructor(): BaseCameraModel {

    companion object {
        private var INSTANCE: Camera2Model? = null
            get() {
                // attribute accessor
                if (field == null) {
                    field = Camera2Model()
                }
                return field
            }
        @Synchronized
        fun get(): Camera2Model {
            return INSTANCE!!
        }
    }

    override fun openCamera(cameraId: String) {
    }

    override fun closeCamera() {
    }
}