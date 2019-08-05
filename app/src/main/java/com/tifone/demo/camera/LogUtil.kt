package com.tifone.demo.camera

import android.util.Log

const val TAG = "tifone"
const val DEBUG: Boolean = true

fun tlogd(msg: String) {
    if (DEBUG) Log.d(TAG, msg)
}

fun tlogd(any: Any, msg: String) {
    if (DEBUG) Log.d(TAG, any.javaClass.simpleName + ": " + msg)
}

fun tlogd(key: String, msg: String) {
    if (DEBUG) Log.d(TAG, "$key: $msg")
}

fun tloge(any: Any, msg: String) {
    Log.e(TAG, any.javaClass.simpleName + ": " + msg)
}

fun tloge(msg: String) {
    Log.e(TAG, msg)
}

fun tloge(key: String, msg: String) {
    Log.e(TAG, "$key: $msg")
}

fun tlogw(msg: String) {
    Log.w(TAG, msg)
}

fun tlogw(any: Any, msg: String) {
    Log.w(TAG, any.javaClass.simpleName + ": " + msg)
}

fun tlogw(key: String, msg: String) {
    Log.w(TAG, "$key: $msg")
}