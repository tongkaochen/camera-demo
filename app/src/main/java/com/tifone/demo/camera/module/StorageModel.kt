package com.tifone.demo.camera.module

import com.tifone.demo.camera.utils.DataWrapper
import java.io.File

interface StorageModel {
    interface ResultCallback {
        fun onComplete(file: File)
        fun onFail(msg: String)
    }
    fun execute(request: DataWrapper)
}