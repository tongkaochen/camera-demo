package com.tifone.demo.camera.agent

class CameraAgent(cameraOperation: CameraOperations) {
    private var mCameraOperation = cameraOperation

    public fun openCamera() {
        mCameraOperation.open()
    }
    public fun startCameraPreview() {
        mCameraOperation.startPreview()
    }
}