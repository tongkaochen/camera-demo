package com.tifone.demo.camera.agent

interface CameraOperations<T> {
    fun open(t: T)
    fun startPreview()
}