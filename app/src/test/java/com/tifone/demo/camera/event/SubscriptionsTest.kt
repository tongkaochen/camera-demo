package com.tifone.demo.camera.event

import com.tifone.demo.camera.utils.DataWrapper
import org.junit.Test

import org.junit.Assert.*

class SubscriptionsTest {

    @Test
    fun subscribe() {
        val sub = Subscriptions.getDefault()
        sub.subscribe(object : Subscriber {
            override fun onReceiveEvent(action: Int, data: DataWrapper) {

            }

            override fun subscribeEvents(): List<Int>? {
                return null
            }

        })
    }

    @Test
    fun postEvent() {
    }

    @Test
    fun unsubscribe() {
    }
}