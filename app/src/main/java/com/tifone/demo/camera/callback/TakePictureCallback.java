package com.tifone.demo.camera.callback;

/**
 * take picture callback
 * when tak picture complete, {@link TakePictureCallback#onTakeComplete(byte[])} will call
 * else: {@link TakePictureCallback#onTakeFail(String)}
 */
public interface TakePictureCallback {
    void onTakeComplete(byte[] data);
    void onTakeFail(String msg);
}
