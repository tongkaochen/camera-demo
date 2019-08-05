package com.tifone.demo.camera.preview

import android.graphics.SurfaceTexture
import android.os.Handler
import android.view.TextureView

class TextureViewController {
    private var mCallback: SurfaceCallback? = null
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
    fun setSurfaceCallback(callback: SurfaceCallback) {
        mCallback = callback
    }
    private fun surfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mCallback?.apply {
            onSurfaceAvailable(surface, width, height)
        }
    }
    private fun surfaceTextureDestroyed() {
        mCallback?.apply {
            onSurfaceDestroy()
        }
    }
    private fun surfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        mCallback?.apply {
            onSurfaceChanged(surface, width, height)
        }
    }

    fun getTextureView(): TextureView {
        return mTextureView
    }

}