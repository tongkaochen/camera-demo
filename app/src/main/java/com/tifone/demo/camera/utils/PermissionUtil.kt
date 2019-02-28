package com.tifone.demo.camera.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

@TargetApi(Build.VERSION_CODES.M)
class PermissionUtil {
    companion object {

        public fun checkCameraPermission(activity: Activity) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
        public fun checkStoragePermission(activity: Activity) {
            if (activity.checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            }
        }

        public fun isCameraPermissionGranted(context: Context): Boolean {
            return context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }
    }
}