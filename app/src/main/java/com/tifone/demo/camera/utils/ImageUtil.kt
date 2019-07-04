package com.tifone.demo.camera.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Size

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
        fun translateDrawableToBitmap(drawable: Drawable): Bitmap? {
            return translateDrawableToBitmap(drawable, 1)
        }

        private fun translateDrawableToBitmap(drawable: Drawable, sampling: Int): Bitmap? {
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            if (width == -1 || height == -1) {
                return null
            }
            // scale bitmap with sampling
            drawable.setBounds(0, 0, width, height)
            val bitmap = Bitmap.createBitmap(width / sampling,
                    height / sampling, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.scale(1.0f / sampling, 1.0f / sampling)
            drawable.draw(canvas)
            return bitmap
        }

        fun translateJpegDataToBitmap(data: ByteArray, size: Size): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            // get the width and height
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            // set the target width and height, simple
            options.inSampleSize = options.outWidth / size.width
            options.inJustDecodeBounds = false
            options.outWidth = size.width
            options.outHeight = size.height
            return BitmapFactory.decodeByteArray(data, 0, data.size, options)
        }
    }
}