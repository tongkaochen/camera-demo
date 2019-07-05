package com.tifone.demo.camera.widget

import android.graphics.*
import android.graphics.drawable.Drawable

class RoundDrawable(bitmap: Bitmap) : Drawable() {
    private var mBitmap = bitmap
    private var mPaint = Paint()
    private var mWidth = 0

    init {
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mPaint.shader = bitmapShader
        mPaint.isAntiAlias = true
        mWidth = Math.min(bitmap.width, bitmap.height)
    }

    override fun draw(canvas: Canvas) {
        val radius = mWidth / 2.0f
        canvas.drawCircle(radius, radius, radius, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }
    fun release() {
        if (!mBitmap.isRecycled) {
            mBitmap.recycle()
        }
    }

}