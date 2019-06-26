package com.tifone.demo.camera.stragety

class CameraApiStrategy(api: ApiLevel) {
    private var apiLevel: ApiLevel = ApiLevel.API2
    fun getApi(): ApiLevel {
        return ApiLevel.API2
    }
    fun getDefault(): ApiLevel {
        return ApiLevel.API2
    }
}