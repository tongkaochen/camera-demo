package com.tifone.demo.camera.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import com.tifone.demo.camera.utils.BitmapUtil
import kotlin.math.min

class RoundDrawable() : Drawable() {
    private var mBitmap: Bitmap? = null
    private var mPaint = Paint()
    private var mWidth = 0

    fun setBitmap(bitmap: Bitmap) {
        recycleBitmap(mBitmap)
        mBitmap = bitmap
        val bitmapShader = BitmapShader(bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mPaint.shader = bitmapShader
        mPaint.isAntiAlias = true
        mWidth = min(bitmap.width, bitmap.height)
        invalidateSelf() // reflash
    }

    // drawable real size
    override fun getIntrinsicWidth(): Int {
        return mWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mWidth
    }

    private fun recycleBitmap(bitmap: Bitmap?) {
        BitmapUtil.recycle(bitmap)
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
        recycleBitmap(mBitmap)
    }

}