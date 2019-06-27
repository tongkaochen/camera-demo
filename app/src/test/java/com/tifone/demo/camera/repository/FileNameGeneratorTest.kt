package com.tifone.demo.camera.repository

import org.junit.Assert.*
import org.junit.Test

class FileNameGeneratorTest {
    @Test
    fun testGenerateFileName() {
        val generator = FileNameGenerator("/data", "magnifier")
        var formatString = generator.generate(FileNameGenerator.TYPE_JPEG)
        assertTrue(formatString.contains("/data/magnifier-"))

        formatString = generator.generate(FileNameGenerator.TYPE_PNG)
        assertTrue(formatString.contains("/data/magnifier-"))

        try {
            generator.generate(3)
            fail("expect to throw an exception")
        } catch (e: IllegalArgumentException) {
        }

    }
}