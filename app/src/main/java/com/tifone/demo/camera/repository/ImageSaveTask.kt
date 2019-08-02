package com.tifone.demo.camera.repository

import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.UiThread
import com.tifone.demo.camera.logd
import com.tifone.demo.camera.module.StorageModel
import com.tifone.demo.camera.utils.DataWrapper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageSaveTask private constructor(): StorageModel {
    private var mHandler: Handler? = null
    private var mThread: HandlerThread? = null
    companion object {
        private var INSTANCE: ImageSaveTask? = null
        private val mLock = Any()
        fun getInstance(): ImageSaveTask {
            if (INSTANCE == null) {
                synchronized(mLock) {
                    if (INSTANCE == null) {
                        INSTANCE = ImageSaveTask()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    init {
        startThread()
    }

    private fun startThread() {
        if (mThread == null) {
            mThread = HandlerThread("image save task thread")
            mThread?.apply {
                start()
                mHandler = Handler(looper)
            }
        }
    }
    private fun stopThread() {
        mThread?.apply {
            quitSafely()
            join()
            mThread = null
            mHandler = null
        }
    }

    override fun release() {
        stopThread()
    }

    @UiThread
    override  fun execute(request: DataWrapper, callback: StorageModel.ResultCallback) {
        logd("current thread: ${Thread.currentThread()}")
        val fileName = request.get(RepositoryKeys.SAVE_PATH)
        val data = request.get(RepositoryKeys.IMAGE_DATA)
        if (fileName == null || data == null) {
            return
        }
        if (mThread == null) {
            startThread()
        }
        mHandler!!.post(SaveImageRunnable(fileName, data, callback))
    }

    private inner class SaveImageRunnable(fileName: String,
                                    data: ByteArray,
                                    callback: StorageModel.ResultCallback)
        : Runnable {
        private var mFileName: String = fileName
        private var mData: ByteArray = data
        private var mResultCallback: StorageModel.ResultCallback? = callback

        override fun run() {
            saveImageToFile()
        }

        private fun saveImageToFile() {

            val imageFile = File(mFileName)
            val parent = imageFile.parentFile
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    mResultCallback?.onFail("mkdir failed")
                    return
                }
            }

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(imageFile)
                fos.write(mData)
                mResultCallback?.onComplete(mFileName)
            } catch (e: IOException) {
                e.printStackTrace()
                mResultCallback?.onFail(e.message)
            } finally {
                if (null != fos) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}
