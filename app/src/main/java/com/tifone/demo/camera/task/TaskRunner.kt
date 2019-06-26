package com.tifone.demo.camera.task

import android.support.annotation.WorkerThread

/**
 * Create by Tifone on 2019/6/26.
 */
interface TaskRunner {
    interface Callback {
        @WorkerThread
        fun onTaskRun(what: Int, any: Any)
    }
    fun setCallback(callback: Callback)
    fun run(what: Int)
    fun run(what: Int, any: Any?)
}