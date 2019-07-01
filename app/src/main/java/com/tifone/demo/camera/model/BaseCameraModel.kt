package com.tifone.demo.camera.model

import android.content.Context
import android.hardware.camera2.CameraDevice
import android.view.Surface
import com.tifone.demo.camera.callback.CameraRequestCallback
import com.tifone.demo.camera.callback.CameraStatusCallback

/**
 * base camera operation interface, you can implement it to do something different
 * include camera open, close ,set flash mode, AE mode, AF mode and so on
 */
interface BaseCameraModel {

    companion object {
        const val OPERATION_OPEN_CAMERA = 1
        const val OPERATION_CLOSE_CAMERA = 2
        const val OPERATION_START_PREVIEW = 3
    }
    fun setCameraStatusCallback(callback:CameraStatusCallback)
    fun openCamera(cameraId: String)
    fun createSession(surface:Surface)
    fun closeCamera()
    fun destroy()
}