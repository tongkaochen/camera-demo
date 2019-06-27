package com.tifone.demo.camera.repository

import android.os.AsyncTask
import com.tifone.demo.camera.module.StorageModel
import com.tifone.demo.camera.utils.DataWrapper

class ImageSaver : StorageModel {
    private var mTask = SaveImageTask()
    private var mFileName: String? = null
    override fun execute(request: DataWrapper) {
        mFileName = request.get(RepositoryKeys.SAVE_PATH)
        mTask.execute()
    }

    class SaveImageTask : AsyncTask<Byte, Int, Void>() {
        override fun doInBackground(vararg params: Byte?): Void? {
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }
    }
}
