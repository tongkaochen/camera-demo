package com.tifone.demo.camera.preview

import android.graphics.SurfaceTexture
import android.os.Handler
import android.view.TextureView

class TextureViewHolder {
    private var mCallbacks: MutableList<SurfaceCallback> = ArrayList()
    private lateinit var mTextureView: TextureView
    interface SurfaceCallback {
        fun onSurfaceAvailable(surface: SurfaceTexture, width: Int, height: Int)
        fun onSurfaceDestroy()
        fun onSurfaceChanged(surface: SurfaceTexture?, width: Int, height: Int)
    }
    fun attachTextureView(texture: TextureView) {
        texture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                surfaceTextureDestroyed()
                return false
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                surfaceTextureAvailable(surface, width, height)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                surfaceTextureSizeChanged(surface, width, height)
            }
        }
    }
    fun registerSurfaceCallback(callback: SurfaceCallback) {
        mCallbacks.add(callback)
    }
    fun unregisterSurfaceCallback(callback: SurfaceCallback) {
        mCallbacks.remove(callback)
    }
    private fun surfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        for (callback in mCallbacks) {
            callback.onSurfaceAvailable(surface, width, height)
        }
    }
    private fun surfaceTextureDestroyed() {
        for (callback in mCallbacks) {
            callback.onSurfaceDestroy()
        }
    }
    private fun surfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        for (callback in mCallbacks) {
            callback.onSurfaceChanged(surface, width, height)
        }
    }

    fun getTextureView(): TextureView {
        return mTextureView
    }

}