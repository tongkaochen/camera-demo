package com.tifone.demo.camera.repository

import android.os.AsyncTask
import com.tifone.demo.camera.module.StorageModel
import com.tifone.demo.camera.utils.DataWrapper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageSaveTask private constructor(): StorageModel {
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

    override  fun execute(request: DataWrapper, callback: StorageModel.ResultCallback) {
        val task = SaveImageTask()
        task.setResultCallback(callback)
        task.execute(request)
    }

    private class SaveImageTask: AsyncTask<DataWrapper, Int, Void>() {
        private var mFileName: String? = null
        private var mData: ByteArray? = null
        private var mResultCallback: StorageModel.ResultCallback? = null
        override fun doInBackground(vararg params: DataWrapper): Void? {
            val request = params[0]
            mFileName = request.get(RepositoryKeys.SAVE_PATH)
            mData = request.get(RepositoryKeys.IMAGE_DATA)
            if (mFileName == null || mData == null) {
                return null
            }
            saveImageToFile()
            cancel(true)
            return null
        }

        fun setResultCallback(callback: StorageModel.ResultCallback) {
            mResultCallback = callback
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
