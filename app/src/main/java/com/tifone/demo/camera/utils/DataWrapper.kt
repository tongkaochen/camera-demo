package com.tifone.demo.camera.utils

import java.lang.StringBuilder

/**
 * generator the request key to wrap the request content
 * you can set some type content to this request map and get the same type
 */
class DataWrapper {
    private var mKeyMap: HashMap<Key<*>, KeyWrapper<*>> = HashMap()

    fun <T> set(key: Key<T>, value: T) {
        mKeyMap[key] = KeyWrapper(value)
    }
    fun <T> get(key: Key<T>): T? {
        val target: KeyWrapper<T> = mKeyMap[key] as KeyWrapper<T>
        return target.value()
    }
//
//    fun <T> getAll(): List<T> {
//        val keys = mKeyMap.keys
//        val result:ArrayList<T> = ArrayList()
//        for (key in keys) {
//            result.add(get(key) as T)
//        }
//        return result
//    }

    private class KeyWrapper<T>(private var value: T) {
        fun value(): T = value
    }
//
//    override fun toString(): String {
//        val builder: StringBuilder = StringBuilder()
//        builder.append("[")
//        val keys = mKeyMap.keys
//        val result:ArrayList<T> = ArrayList()
//        for (key in keys) {
//            result.add(get(key) as T)
//        }
//        builder.append("]")
//        return super.toString()
//    }

    /**
     * key has name and type attribute
     * name is the key
     * type is target data's type
     */
    class Key<T>(private var name: String, private var type: Class<T>) {
        override fun hashCode(): Int {
            return name.hashCode() xor type.hashCode()
        }
        override fun equals(other: Any?): Boolean {
            if (other == null) {
                return false
            }
            if (other is Key<*>) {
                return other.name == name && other.type == type
            }
            return false
        }
    }
}