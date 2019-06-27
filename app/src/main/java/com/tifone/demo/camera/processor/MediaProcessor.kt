package com.tifone.demo.camera.processor

import android.graphics.Bitmap

interface IImageProcessor {
    fun yuv2Rgb(): Array<Byte>
    fun byte2Bitmap():Bitmap
}
interface IVideoProcessor {

}
interface ITextProcessor {

}