package com.tifone.demo.camera.task

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import java.util.*
import java.util.concurrent.ThreadPoolExecutor

/**
 * Create by Tifone on 2019/6/26.
 */
class CameraAsyncRunner(msg: String) : TaskRunner{
    private var mHandlerThread: HandlerThread = HandlerThread(msg)
    private var mHandler: CameraHandler
    private var mMessage: Message

    init {
        mHandlerThread.start()
        mHandler = CameraHandler(mHandlerThread.looper)
        mMessage = mHandler.obtainMessage()
    }
    override fun setCallback(callback: TaskRunner.Callback) {
        mHandler.setCallback(callback)
    }

    private fun wrapMessage(what: Int, any: Any?) {
        mMessage = mHandler.obtainMessage(what)
        any?.apply {
            mMessage.obj = this
        }
    }

    override fun run(what: Int) {
        run(what, null)
    }
    override fun run(what: Int, any: Any?) {
        wrapMessage(what, any)
        // 发送消息到异步线程
        mHandler.sendMessage(mMessage)
    }
    class CameraHandler(looper: Looper) : Handler(looper) {
        private var mCallback: TaskRunner.Callback? = null
        fun setCallback(callback: TaskRunner.Callback) {
            mCallback = callback
        }
        override fun handleMessage(msg: Message) {
            mCallback?.apply {
                onTaskRun(msg.what, msg.obj)
            }
        }
    }
}