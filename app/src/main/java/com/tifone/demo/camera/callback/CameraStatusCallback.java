package com.tifone.demo.camera.callback;

public interface CameraStatusCallback {
    void onCameraOpened();
    void onCameraError();
    void onCameraDisconnected();
    void onCameraClosed();
}
