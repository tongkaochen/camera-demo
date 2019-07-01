package com.tifone.demo.camera.camera

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size

class CameraInfo(context: Context, cameraId: CameraId) {
    private var mContext = context
    private val mCameraId = cameraId.value()
    private val mCameraManager: CameraManager =
            mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun <T> getAvailablePreviewSize(target: Class<T>): Array<Size>? {
        try {
            val cc: CameraCharacteristics =
                    mCameraManager.getCameraCharacteristics(mCameraId)
            val scalerStreamMap: StreamConfigurationMap? =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            return scalerStreamMap?.getOutputSizes(target)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }
    private fun getCharacteristics(): CameraCharacteristics? {
        try {
            mCameraManager.getCameraCharacteristics(mCameraId)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    fun getSensorOrientation(): Int {
        return getCharacteristics()
                ?.get(CameraCharacteristics.SENSOR_ORIENTATION)
                ?: -1
    }
}