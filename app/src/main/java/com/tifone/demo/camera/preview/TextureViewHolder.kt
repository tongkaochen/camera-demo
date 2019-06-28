package com.tifone.demo.camera.preview

import android.graphics.SurfaceTexture
import android.view.TextureView
import android.widget.TextView
import com.tifone.demo.camera.presenter.TexturePresenter

class PreviewSurfaceHolder {
    private var mCallbacks: MutableList<SurfaceCallback> = ArrayList()
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
        mCallbacks.add(callback)
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

}