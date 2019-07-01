package com.tifone.demo.camera.event

import android.os.Handler
import android.os.HandlerThread
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.tifone.demo.camera.utils.DataWrapper
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class SubscriptionsTest {

    @Test
    fun subscribe() {
        val sub = Subscriptions.getDefault()
        var countClick = 0
        var client = 0
        val thread = HandlerThread("aaa")
        thread.start()
        val handler = Handler(thread.looper)
        for (i in 1 .. 40) {
            client += if (i < Subscriptions.SUBSCRIPTION_LIMIT_SIZE) {
                i
            } else {
                Subscriptions.SUBSCRIPTION_LIMIT_SIZE
            }
            sub.subscribe(object : Subscriber {
                override fun onReceiveEvent(action: Subscriptions.Event, data: DataWrapper?) {
                    Log.d("tifone", "onReceiveEvent")
                    handler.post {
                        Thread.sleep(500)
                    }
                    countClick++
                }

                override fun subscribeEvents(): List<Subscriptions.Event> {
                    return listOf(Subscriptions.Event.ALL)
                }

            })
            sub.postEvent(Subscriptions.Event.CLICK, null)
            Log.d("tifone", "count = $client")
            assertEquals(countClick, client)
        }
    }

    @Test
    fun postEvent() {
    }

    @Test
    fun unsubscribe() {

    }
}