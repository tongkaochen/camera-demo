package com.tifone.demo.camera.module

import com.tifone.demo.camera.view.CameraUI

class ModuleManager(view: CameraUI, moduleId: ModuleID) {
    private var mCameraUI = view
    private var mCurrentModule: BaseModule? = null

    fun initModule(id: ModuleID) {
        mCurrentModule = getModuleFor(id)
        mCurrentModule?.create()
    }
    private fun getModuleFor(id: ModuleID): BaseModule {
        return when(id) {
            ModuleID.PHOTO -> PhotoModule.getInstance(mCameraUI)
            ModuleID.VIDEO -> VideoModule.getInstance(mCameraUI)
        }
    }
    fun switchModule(moduleId: ModuleID) {

    }
    fun getCurrentModule(): BaseModule? {
        return mCurrentModule
    }
    fun destoryModule() {
        mCurrentModule?.destroy()
    }
}