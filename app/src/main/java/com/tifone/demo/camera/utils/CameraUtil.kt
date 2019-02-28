package com.tifone.demo.camera.utils

class CameraUtil {
    companion object {
        public var cameraId: String = "0"
        public fun requestForeCamera() {
            cameraId = "1"
        }
        public fun requestBackCamera() {
            cameraId = "0"
        }
    }

}