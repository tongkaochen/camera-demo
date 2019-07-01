package com.tifone.demo.camera.event

import android.os.Handler
import android.util.Log
import com.tifone.demo.camera.utils.DataWrapper
import kotlin.collections.ArrayList

class Subscriptions private constructor(){

    private var mSubscriptions: MutableSet<Subscriber> = HashSet()
    private val mSubscriberLock = Any()
    private var mPendingList: ArrayList<PostItem> = ArrayList()

    private var mInPosting = false

    enum class Event {
        ALL, // all event.
        SHUTTER_CLICK,
        CLICK
    }
    companion object {
        private const val TAG = "Subscriptions"
        private const val DEBUG = true
        const val SUBSCRIPTION_LIMIT_SIZE = 30
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
                return
            }
            mSubscriptions.add(subscriber)
        }
    }
    private fun addToPendingListLocked(item: PostItem) {
        if (DEBUG) Log.d(TAG, "addToPendingListLocked : $item")
        mPendingList.add(item)
        if (DEBUG) Log.d(TAG, "pending list size : ${mPendingList.size}")
    }
    private fun resolvePendingDataLocked() {
        if (DEBUG) Log.d(TAG, "resolvePendingDataLocked : ${mPendingList.size}")
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
    private fun isSubscribed(eventList: List<Event>, target: Event): Boolean {
        return eventList.contains(target) or eventList.contains(Event.ALL)
    }
    private fun postEventLocked(action: Event, data: DataWrapper?) {
        if (DEBUG) Log.d(TAG, "postEventLocked : ${mSubscriptions.size}")
        for (subscriber in mSubscriptions) {
            mInPosting = true
            val events = subscriber.subscribeEvents()
            val isSubscribed =  isSubscribed(events, action)
            val shouldDispatch = action == Event.ALL || isSubscribed
            if (!shouldDispatch) {
                continue
            }
            if (DEBUG) Log.d(TAG, "onReceiveEvent : $subscriber")
            subscriber.onReceiveEvent(action, data)

        }
        mInPosting = false
        resolvePendingDataLocked()
    }

    /** post message to the all subscribed subscriber
     * this method is Synchronized, please do not do the time-consuming time
     */
    @Synchronized
    fun postEvent(action: Event, data: DataWrapper?) {
        if (DEBUG) Log.d(TAG, "postEvent : $action")
        postEventLocked(action, data)
    }

    @Synchronized
    fun postEvent(action: Event) {
        if (DEBUG) Log.d(TAG, "postEvent : $action")
        postEventLocked(action, null)
    }

    @Deprecated("need optimize")
    @Synchronized
    fun postEventAsync(handler: Handler, action: Event, data: DataWrapper?) {
        // TODO
        if (DEBUG) Log.d(TAG, "postEventAync : $action")
        if (mInPosting) {
            if (DEBUG) Log.d(TAG, "add to pending list : $action")
            addToPendingListLocked(PostItem(action, data))
            return
        }

        handler.post {
            if (DEBUG) Log.d(TAG, "run : $action")
            postEventLocked(action, data)
        }
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

    private class PostItem(val event: Event, val data: DataWrapper?) {
        override fun toString(): String {
            return "event: $event "
        }
    }
}