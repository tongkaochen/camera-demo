package com.tifone.demo.camera.model

import android.content.Context

/**
 * base camera operation interface, you can implement it to do something different
 * include camera open, close ,set flash mode, AE mode, AF mode and so on
 */
interface BaseCameraModel {

    companion object {
        const val operation_open_camera = 1
        const val operation_close_camera = 2
        const val operation_start_preview = 3
    }
    fun openCamera(cameraId: String)
    fun closeCamera()
    fun destroy()
}