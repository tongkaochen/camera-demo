package com.tifone.demo.camera.repository

import java.text.SimpleDateFormat
import java.util.*

class FileNameGenerator(rootPath: String, baseName: String) {
    private val mRootPath = rootPath
    private val mBaseName = baseName
    companion object {
        private const val PATTERN = "yyyy_MM_dd_HHmmss"
        const val TYPE_JPEG = 1
        const val TYPE_PNG = 2
        const val JPEG_SUFFIX = ".jpg"
        const val PNG_SUFFIX = ".png"
    }
    private var mDateFormat: SimpleDateFormat

    init {
        mDateFormat = SimpleDateFormat(PATTERN)

    }

    private fun generateTimestamp(): String {
        val latest = Date()
        return mDateFormat.format(latest)
    }

    fun generate(type: Int): String {
        val builder = StringBuilder(mRootPath)
        if (!mRootPath.endsWith("/")) {
            builder.append("/")
        }
        builder.append(mBaseName)
                .append("-")
                .append(generateTimestamp())
                .append(getTypeString(type))
        return builder.toString()
    }

    private fun getTypeString(typeId: Int): String {
        return when (typeId) {
            TYPE_JPEG -> JPEG_SUFFIX
            TYPE_PNG -> PNG_SUFFIX
            else -> throw IllegalArgumentException("Unknown suffix type id: $typeId")
        }
    }
}