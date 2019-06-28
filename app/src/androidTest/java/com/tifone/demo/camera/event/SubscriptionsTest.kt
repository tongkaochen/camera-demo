package com.tifone.demo.camera.event

import com.tifone.demo.camera.utils.DataWrapper
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

class SubscriptionsTest {

    @Test
    fun subscribe() {
        val sub = Subscriptions.getDefault()
        var countClick = 0
        sub.subscribe(object : Subscriber {
            override fun onReceiveEvent(action: Int, data: DataWrapper?) {
                countClick++
            }

            override fun subscribeEvents(): List<Int>? {
                return listOf(Subscriptions.Event.ALL)
            }

        })
        sub.postEvent(Subscriptions.Event.CLICK, null)
        assertEquals(countClick, 1)
    }

    @Test
    fun postEvent() {
    }

    @Test
    fun unsubscribe() {
    }
}