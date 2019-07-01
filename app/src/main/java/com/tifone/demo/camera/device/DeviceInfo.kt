package com.tifone.demo.camera.device

import android.graphics.Point
import android.util.Size
import android.view.Display

class DeviceInfo private constructor() {
    // device screen size
    private var mScreenSize = Size(0,0)
    private var mRotation = -1
    companion object {
        private var INSTANCE: DeviceInfo? = null
        private val mAny = Any()
        @Synchronized
        fun get(): DeviceInfo {
            if (INSTANCE == null) {
                synchronized(mAny) {
                    if (INSTANCE == null) {
                        INSTANCE = DeviceInfo()
                    }
                }
            }
            return INSTANCE!!
        }
    }
    fun init() {

    }
    fun getScreenSize(): Size {
        return mScreenSize
    }
    fun getRotation(): Int {
        return mRotation
    }
    fun updateDisplayInfo(display: Display) {
        val realSize = Point()
        display.getRealSize(realSize)
        mScreenSize = Size(realSize.x, realSize.y)
        mRotation = display.rotation
    }
}