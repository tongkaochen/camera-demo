package com.tifone.demo.camera.model

import android.util.SparseIntArray
import android.view.Surface

class CameraOrientationHelper {
    companion object {
        private val ORIENTATIONS = SparseIntArray()
    }
    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    fun getOrentation(deviceRotation: Int, sensorRotation: Int): Int {
        return (ORIENTATIONS[deviceRotation] + sensorRotation + 270) % 360
    }

}