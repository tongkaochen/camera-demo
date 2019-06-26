package com.tifone.demo.camera.agent

class CameraAgent<T>(cameraOperation: CameraOperations<T>) {
    private var mCameraOperation = cameraOperation

    public fun openCamera(t: T) {
        mCameraOperation.open(t)
    }
    public fun startCameraPreview() {
        mCameraOperation.startPreview()
    }
}