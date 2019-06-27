package com.tifone.demo.camera.utils

import org.junit.Test

import org.junit.Assert.*

class DataWrapperTest {


    @Test
    fun test_set_get() {
        val wrapper = DataWrapper()
        val intKey: DataWrapper.Key<Int> = DataWrapper.Key("Test.int", Int::class.java)
        val stringKey: DataWrapper.Key<String> = DataWrapper.Key("Test.string", String::class.java)
        val arrayKey: DataWrapper.Key<Array<Int>> = DataWrapper.Key("Test.array", Array<Int>::class.java)
        wrapper.set(intKey, 100)
        wrapper.set(stringKey, "test-string")
        wrapper.set(arrayKey, Array(5) {5})

        assertEquals(wrapper.get(intKey), 100)
        assertEquals(wrapper.get(stringKey), "test-string")
        assertEquals(wrapper.get(arrayKey)?.get(1), 5)
        val string2Key: DataWrapper.Key<String> = DataWrapper.Key("Test.string", String::class.java)
        assertEquals(string2Key, stringKey)
    }
}