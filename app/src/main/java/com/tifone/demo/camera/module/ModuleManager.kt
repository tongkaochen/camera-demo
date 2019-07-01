package com.tifone.demo.camera.module

import com.tifone.demo.camera.view.CameraUI

class ModuleManager private constructor() {
    private var mCurrentModule: BaseModule? = null

    fun initModule(view: CameraUI, id: ModuleID) {
        mCurrentModule = createModuleFor(view, id)
        mCurrentModule?.create()
    }
    private fun createModuleFor(view: CameraUI, id: ModuleID): BaseModule {
        return when(id) {
            ModuleID.PHOTO -> PhotoModule(view)
            ModuleID.VIDEO -> VideoModule(view)
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