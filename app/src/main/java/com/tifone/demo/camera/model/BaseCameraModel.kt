package com.tifone.demo.camera.model

import android.content.Context
import android.hardware.camera2.CameraDevice
import android.view.Surface
import com.tifone.demo.camera.callback.CameraRequestCallback
import com.tifone.demo.camera.callback.CameraStatusCallback
import com.tifone.demo.camera.callback.TakePictureCallback
import com.tifone.demo.camera.camera.CameraInfo

/**
 * base camera operation interface, you can implement it to do something different
 * include camera open, close ,set flash mode, AE mode, AF mode and so on
 */
interface BaseCameraModel {
    fun setCameraStatusCallback(callback:CameraStatusCallback)
    fun openCameraAsync(cameraInfo: CameraInfo)
    fun startPreviewAsync(surface:Surface)
    fun closeCameraAsync()
    fun destroy()
    fun takePictureAsync()
    fun setTakePictureCallback(callback: TakePictureCallback)
}