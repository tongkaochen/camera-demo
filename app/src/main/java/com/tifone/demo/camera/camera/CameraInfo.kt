package com.tifone.demo.camera.camera

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size
import com.tifone.demo.camera.tlogd
import com.tifone.demo.camera.preview.PreviewSizeHelper

class CameraInfo(context: Context) {
    private var mContext = context
    private val mCameraManager: CameraManager =
            mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val mCharacteristics = HashMap<String, CameraCharacteristics>()
    private var mCurrentId: String = CameraId.ID_BACK.value()
    private var mCurrentPreviewSize: Size? = null

    init {
        init()
    }
    /**
     * create characteristics for all camera id
     */
    private fun init() {
        val cameraIdList = mCameraManager.cameraIdList
        for (id in cameraIdList) {
            val characteristics = getCharacteristics(id)
            characteristics?.apply {
                mCharacteristics[id] = this
            }
        }
    }

    /**
     * set camera id, the id must beyong to cameraIdList
     */
    fun setCameraId(cameraId: CameraId) {
        val id = cameraId.value()
        if (mCameraManager.cameraIdList.contains(id)) {
            if (mCurrentId != id) {
                mCurrentId = cameraId.value()
                mCurrentPreviewSize = null
            }
        }
    }

    /**
     * please call {@see setCameraId} before
     * @return all the available preview size, include the highResolution size
     */
    private fun <T> getAvailablePreviewSize(target: Class<T>): Array<Size>? {
        try {
            val cc: CameraCharacteristics = mCharacteristics[mCurrentId]!!
            val scalerStreamMap: StreamConfigurationMap? =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            return scalerStreamMap?.getOutputSizes(target)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * current preview that used before
     */
    fun getCurrentPreviewSize(): Size? {
        return mCurrentPreviewSize
    }

    fun <T> getPreviewSize(target: Class<T>, aspectRatio: Float): Size? {
        val availablePreviewSize = getAvailablePreviewSize(target) ?: return null
        tlogd("availablePreviewSize = $availablePreviewSize")

        if (mCurrentPreviewSize == null) {
            mCurrentPreviewSize = PreviewSizeHelper().getMatchSize(availablePreviewSize, aspectRatio)
        }
        return mCurrentPreviewSize
    }

    /**
     * current opened camera id
     */
    fun getCameraId(): String {
        return mCurrentId
    }

    /**
     * all the available size of image format, use to capture image
     */
    fun getOutputSizes(format: Int): List<Size> {
        val characteristics = mCharacteristics[mCurrentId]!!
        val streamMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        return CaptureHelper.getOutputSizes(streamMap, format)
    }

    /**
     * get the target image size for the specify aspect ratio
     */
    fun getOutputImageSize(format: Int, aspectRatio: Float):Size {
        val availableSize = getOutputSizes(format)
        return CaptureHelper.getCaptureImageSize(availableSize, aspectRatio)
    }

    private fun getCharacteristics(id: String): CameraCharacteristics? {
        try {
            return mCameraManager.getCameraCharacteristics(id)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * camera sensor orientation
     */
    fun getSensorOrientation(): Int {
        return mCharacteristics[mCurrentId]
                ?.get(CameraCharacteristics.SENSOR_ORIENTATION)
                ?: -1
    }
}