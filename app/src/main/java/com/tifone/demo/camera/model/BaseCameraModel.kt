package com.tifone.demo.camera.model

/**
 * base camera operation interface, you can implement it to do something different
 * include camera open, close ,set flash mode, AE mode, AF mode and so on
 */
interface BaseCameraModel {
    fun openCamera(cameraId: String)
    fun closeCamera()
}