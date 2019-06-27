package com.tifone.demo.camera.camera

class CameraId(id: String) {
    private var mCameraId = id

    companion object {
        private const val BACK = "0"
        private const val FRONT = "1"
        val ID_BACK = CameraId(BACK)
        val ID_FRONT = CameraId(FRONT)
    }
    fun value(): String = mCameraId
    override fun equals(other: Any?): Boolean {
        if (other is CameraId) {
            return mCameraId == other.mCameraId
        }
        return false
    }
}