package com.tifone.demo.camera.utils

import android.os.Environment


fun getExternalPath(): String {
    return Environment.getExternalStoragePublicDirectory("").toString() + "/camera-demo/"
}