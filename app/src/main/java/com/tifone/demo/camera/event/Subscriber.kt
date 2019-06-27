package com.tifone.demo.camera.event

import com.tifone.demo.camera.utils.DataWrapper

interface Subscriber {
    fun onReceiveEvent(action: Int, data: DataWrapper)
    fun subscribeEvents(): List<Int>?
}