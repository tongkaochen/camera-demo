package com.tifone.demo.camera.callback;

/**
 * take picture callback
 * when tak picture complete, {@link TakePictureCallback#onTakenComplete(byte[])} will call
 * else: {@link TakePictureCallback#onTakeFailed(String)}
 */
public interface TakePictureCallback {
    void onTakenComplete(byte[] data);
    void onTakeFailed(String msg);
}
