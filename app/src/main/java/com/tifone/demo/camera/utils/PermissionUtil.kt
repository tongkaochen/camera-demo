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

        fun isCameraPermissionGranted(context: Context): Boolean {
            return isPermissionGranted(context, Manifest.permission.CAMERA)
        }
        fun isPermissionGranted(context: Context, permission: String): Boolean {
            return context.checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }
}