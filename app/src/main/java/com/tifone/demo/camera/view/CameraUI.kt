package com.tifone.demo.camera.view

import android.graphics.Bitmap
import android.util.Size
import com.tifone.demo.camera.preview.TextureViewController

interface CameraUI :IView{
    fun updateShutterStatus(status: Int)
    fun getPreviewSurfaceHolder(): TextureViewController
    fun getUIAspectRatio(): Float
    fun setPreviewAspectRatio(ratio: Float)
    fun updateThumb(bitmap: Bitmap)
    fun getThumbSize(): Size
}