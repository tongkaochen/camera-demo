package com.tifone.demo.camera.module

/**
 * every module should implement this interface.
 * we would use module to communication with camera device.
 * also, it manage some camera data and logic.
 * include save image, save video and so on
 */
interface BaseModule {
    fun getId(): ModuleID
    fun create()
    fun start()
    fun pause()
    fun destroy()
}