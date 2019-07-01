package com.tifone.demo.camera.callback;

public interface CameraRequestCallback<T> {
    void onComplete(T result);
    void onFail(String msg);
}
