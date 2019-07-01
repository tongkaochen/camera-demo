package com.tifone.demo.camera.view

import com.tifone.demo.camera.preview.TextureViewHolder

interface CameraUI :IView{
    fun updateShutterStatus(status: Int)
    fun getPreviewSurfaceHolder(): TextureViewHolder
}