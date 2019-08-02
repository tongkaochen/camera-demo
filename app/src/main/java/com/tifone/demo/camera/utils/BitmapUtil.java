package com.tifone.demo.camera.utils;

import android.graphics.Bitmap;

public class BitmapUtil {
    public static void recycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
