package com.tifone.demo.camera.utils

class SingletonHolder<out O, in I>(creator: (I) -> O) {
    private var mCreator: ((I) -> O)? = creator
    @Volatile
    private var INSTANCE: O? = null

    fun getInstance(inputTarget: I): O {
        val value = INSTANCE
        if (value != null) {
            return value
        }
        return synchronized(this) {
            val newValue = INSTANCE
            if (newValue != null) {
                newValue
            } else {
                val created = mCreator!!(inputTarget)
                INSTANCE = created
                mCreator = null
                created
            }
        }
    }
}