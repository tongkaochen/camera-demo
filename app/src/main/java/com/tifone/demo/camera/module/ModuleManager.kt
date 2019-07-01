package com.tifone.demo.camera.module

import com.tifone.demo.camera.logd
import com.tifone.demo.camera.view.CameraUI

class ModuleManager private constructor() {
    companion object {
        private var INSTANCE: ModuleManager? = null
        private val mLock = Any()
        fun getInstance(): ModuleManager {
            if (INSTANCE == null) {
                synchronized(mLock) {
                    if (INSTANCE == null) {
                        INSTANCE = ModuleManager()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private var mCurrentModule: BaseModule? = null

    fun initModule(view: CameraUI, id: ModuleID) {
        mCurrentModule = createModuleFor(view, id)
        mCurrentModule!!.create()
    }
    private fun createModuleFor(view: CameraUI, id: ModuleID): BaseModule {
        return when(id) {
            ModuleID.PHOTO -> PhotoModule(view)
            ModuleID.VIDEO -> VideoModule(view)
        }
    }
    fun startModule() {
        mCurrentModule?.apply {
            start()
        }?: logd("current module is not initialized")
    }

    fun stopModule() {
        mCurrentModule?.apply {
            stop()
        }?: logd("current module is not initialized")
    }

    fun switchModule(moduleId: ModuleID) {

    }
    fun getCurrentModule(): BaseModule? {
        return mCurrentModule
    }
    fun destroyModule() {
        mCurrentModule?.destroy()
    }

    private fun logd(msg: String) {
        logd(this, msg)
    }
}