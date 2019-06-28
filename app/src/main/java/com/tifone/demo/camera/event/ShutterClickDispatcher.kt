package com.tifone.demo.camera.event

import android.view.View

class ShutterDispatcher{
    interface ShutterClickListener {
        fun onShutterClicked(view: View)
    }
    private var mListener: MutableList<ShutterClickListener> = ArrayList()
    companion object {
        private var INSTANCE: ShutterDispatcher? = null
        private val mAny = Any()
        @Synchronized
        fun getDefault(): ShutterDispatcher {
            if (INSTANCE == null) {
                synchronized(mAny) {
                    if (INSTANCE == null) {
                        INSTANCE = ShutterDispatcher()
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
    fun registerShutterClickLisenter(listener: ShutterClickListener) {
        mListener.remove(listener)
    }
}