package com.tifone.demo.camera.utils

import android.graphics.ImageFormat
import android.media.Image

class ImageUtil {
    companion object {
        fun imageToBytes(image: Image): ByteArray {
            return when(image.format) {
                ImageFormat.JPEG -> toJPEGByte(image)
                else -> toJPEGByte(image)
            }
        }
        private fun toJPEGByte(image: Image): ByteArray {
            val plane: Image.Plane = image.planes[0]
            val byteBuffer = plane.buffer
            val bytes = ByteArray(byteBuffer.remaining())
            byteBuffer.get(bytes)
            byteBuffer.rewind()
            return bytes
        }
    }
}