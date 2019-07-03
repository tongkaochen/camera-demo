package com.tifone.demo.camera.media

import android.media.MediaActionSound

class CameraEffectPlayer {
    private var mSound:MediaActionSound? = MediaActionSound()

    init {
        load()
    }
    private fun load() {
        mSound?.also {
            it.load(MediaActionSound.SHUTTER_CLICK)
        }
    }
    fun playShutterEffect() {
        play(MediaActionSound.SHUTTER_CLICK)
    }

    @Synchronized
    private fun play(action: Int) {
        mSound?.also {
            when(action) {
                MediaActionSound.SHUTTER_CLICK -> it.play(action)
            }
        }
    }

    fun release() {
        mSound?.apply {
            release()
            mSound = null
        }
    }
}