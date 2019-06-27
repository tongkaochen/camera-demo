package com.tifone.demo.camera.event

import android.util.Log
import com.tifone.demo.camera.utils.DataWrapper
import java.util.*
import kotlin.collections.ArrayList

class Subscriptions {

    private var mSubscriptions: MutableSet<Subscriber> = HashSet()
    private val mSubscriberLock = Any()
    private var mPendingList: ArrayList<PostItem> = ArrayList()

    private var mInPosting = false

    object Event {
        const val ALL = 0
    }
    companion object {
        private const val TAG = "Subscriptions"
        private const val DEBUG = true
        private const val SUBSCRIPTION_LIMIT_SIZE = 30
        private var INSTANCE: Subscriptions? = null
        private val mAny = Any()
        @Synchronized
        fun getDefault(): Subscriptions {
            if (INSTANCE == null) {
                synchronized(mAny) {
                    if (INSTANCE == null) {
                        INSTANCE = Subscriptions()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    // Let subscriber subscribe some events which specified with its subscribeEvents
    fun subscribe(subscriber: Subscriber) {
        synchronized(mSubscriberLock) {
            if (DEBUG) Log.d(TAG, "subscribe : $subscriber")
            if (mSubscriptions.size >= SUBSCRIPTION_LIMIT_SIZE) {
                Log.e(TAG, "subscriptions is out of limit, subscribe $subscriber fail")
            }
            mSubscriptions.add(subscriber)
        }
    }
    private fun addToPendingListLocked(item: PostItem) {
        if (DEBUG) Log.d(TAG, "addToPendingListLocked : $item")
        mPendingList.add(item)
    }
    private fun resolvePendingDataLocked() {
        if (mPendingList.size < 1) {
            return
        }
        val pendingList: ArrayList<PostItem> = ArrayList()
        pendingList.addAll(mPendingList)
        mPendingList.clear()
        for (item in pendingList) {
            postEventLocked(item.event, item.data)
        }
        pendingList.clear()
    }
    private fun postEventLocked(action: Int, data: DataWrapper) {
        for (subscription in mSubscriptions) {
            mInPosting = true
            val events = subscription.subscribeEvents()
            val isSubscribed = events?.contains(action) ?: false
            val shouldDispatch = action == Event.ALL || isSubscribed
            if (!shouldDispatch) {
                continue
            }
            subscription.onReceiveEvent(action, data)
        }
        mInPosting = false
        resolvePendingDataLocked()
    }

    // post message to target type
    @Synchronized
    fun postEvent(action: Int, data: DataWrapper) {
        if (mInPosting) {
            addToPendingListLocked(PostItem(action, data))
            return
        }
        postEventLocked(action, data)
    }

    // unsubscribe for all the subscriber,
    // subscriber would not receive any event after that
    fun unsubscribe(subscriber: Subscriber) {
        synchronized(mSubscriberLock) {
            if (mSubscriptions.contains(subscriber)) {
                mSubscriptions.remove(subscriber)
            }
        }
    }

    private class PostItem(val event: Int, val data: DataWrapper) {
        override fun toString(): String {
            return "event: $event "
        }
    }
}