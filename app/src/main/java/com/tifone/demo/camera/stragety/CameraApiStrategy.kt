package com.tifone.demo.camera.stragety

import com.tifone.demo.camera.module.ModuleID

class CameraApiStrategy(moduleId: ModuleID) {
    private var mTargetModuleId = moduleId
    private var apiLevel: ApiLevel = ApiLevel.API2
    fun setTargetModuleId(moduleId: ModuleID) {
        mTargetModuleId = moduleId
    }
    fun getApi(): ApiLevel {
        return when(mTargetModuleId) {
            ModuleID.PHOTO, ModuleID.VIDEO -> ApiLevel.API2
            else -> ApiLevel.API1
        }
    }
    fun getDefault(): ApiLevel {
        return ApiLevel.API2
    }
}