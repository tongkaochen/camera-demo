package com.tifone.demo.camera.event

import android.view.View

class ShutterClickDispatcher {
    interface ShutterClickListener {
        fun onShutterClicked(view: View)
    }
    private var mListener: MutableList<ShutterClickListener> = ArrayList()
    companion object {
        private var INSTANCE: ShutterClickDispatcher? = null
        private val mAny = Any()
        @Synchronized
        fun getDefault(): ShutterClickDispatcher {
            if (INSTANCE == null) {
                synchronized(mAny) {
                    if (INSTANCE == null) {
                        INSTANCE = ShutterClickDispatcher()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    fun onShutterClicked(view: View) {
        for (listener in mListener) {
            listener.onShutterClicked(view)
        }
    }
    fun register(listener: ShutterClickListener) {
        mListener.add(listener)
    }
    fun unregister(listener: ShutterClickListener) {
        mListener.remove(listener)
    }
}