package com.tifone.demo.camera.event

import com.tifone.demo.camera.utils.DataWrapper

interface Subscriber {
    fun onReceiveEvent(action: Subscriptions.Event, data: DataWrapper?)
    fun subscribeEvents(): List<Subscriptions.Event>
}