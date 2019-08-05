package com.tifone.demo.camera.model

import com.tifone.demo.camera.utils.DataWrapper

interface StorageModel {
    interface ResultCallback {
        fun onComplete(result: String?)
        fun onFail(msg: String?)
    }
    fun execute(request: DataWrapper, callback: ResultCallback)
    fun release()
}