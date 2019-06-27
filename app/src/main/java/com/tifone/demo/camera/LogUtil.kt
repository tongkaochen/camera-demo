package com.tifone.demo.camera

import android.util.Log

const val TAG = "tifone"
const val DEBUG: Boolean = true

fun logd(msg: String) {
    if (DEBUG) Log.d(TAG, msg)
}

fun logd(any: Any, msg: String) {
    if (DEBUG) Log.d(TAG, any.javaClass.simpleName + ": " + msg)
}

fun logd(key: String, msg: String) {
    if (DEBUG) Log.d(TAG, "$key: $msg")
}

fun loge(any: Any, msg: String) {
    Log.e(TAG, any.javaClass.simpleName + ": " + msg)
}

fun loge(msg: String) {
    Log.e(TAG, msg)
}

fun loge(key: String, msg: String) {
    Log.e(TAG, "$key: $msg")
}

fun logw(msg: String) {
    Log.w(TAG, msg)
}

fun logw(any: Any, msg: String) {
    Log.w(TAG, any.javaClass.simpleName + ": " + msg)
}

fun logw(key: String, msg: String) {
    Log.w(TAG, "$key: $msg")
}