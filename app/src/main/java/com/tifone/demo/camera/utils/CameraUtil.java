package com.tifone.demo.camera.utils;

import android.support.annotation.NonNull;
import android.util.Size;

public class CameraUtil {
    public static float getAspectRatio(@NonNull Size size) {
        return (float) size.getWidth() / size.getHeight();
    }
}
