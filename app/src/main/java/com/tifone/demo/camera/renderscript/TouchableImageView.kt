package com.tifone.demo.camera.renderscript

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView

class TouchableImageView : ImageView {
    private lateinit var mUtil: RsUtil
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr:AttributeSet?, defStyleAttr: Int):
            super(context, attr, defStyleAttr)

    public fun setRsUtil(util: RsUtil) {
        mUtil = util
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var atX: Float = 0f
        var atY: Float = 0f
        event?.run {
            atX = x
            atY = y
        }
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {

            }
        }
        Log.e("tifone","$atX   :    $atY")
        setImageBitmap(mUtil.render(atX / width, atY / height))
        return super.onTouchEvent(event)
    }
}