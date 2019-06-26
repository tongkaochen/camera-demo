package com.tifone.demo.camera.module

interface BaseModule {
    fun getId(): ModuleID
    fun create()
    fun start()
    fun pause()
    fun destroy()
}