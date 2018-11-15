package com.tifone.demo.camera.renderscript

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.renderscript.*
import android.util.Log
import com.tifone.demo.camera.R
import com.tifone.demo.camera.rs.ScriptC_addint
import com.tifone.demo.camera.rs.ScriptC_single_source

class RsUtil(context: Context) {
    private var mRenderScript: RenderScript = RenderScript.create(context)
    private val script = ScriptC_single_source(mRenderScript)
    private val mAddIntScript = ScriptC_addint(mRenderScript)
    private val mContext = context

    public fun render(xRatio: Float, yRatio: Float): Bitmap {
        val inputBitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.pic_demo)
        val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val inputAllocation: Allocation = Allocation.createFromBitmap(mRenderScript, inputBitmap)
        val outputAllocation: Allocation = Allocation.createTyped(mRenderScript, inputAllocation.type)
        script._atX = (xRatio * inputBitmap.width).toInt()
        script._atY = (yRatio * inputBitmap.height).toInt()
        script.invoke_process(inputAllocation, outputAllocation)

        outputAllocation.copyTo(outputBitmap)

        inputBitmap.recycle()
        mRenderScript.destroy()
        return outputBitmap
    }
    public fun renderBlur(): Bitmap {
        val inputBitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.pic_demo)
        val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val inputAllocation: Allocation = Allocation.createFromBitmap(mRenderScript, inputBitmap)
        val outputAllocation: Allocation = Allocation.createTyped(mRenderScript, inputAllocation.type)
        val blur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript))
        blur.setRadius(25.0f)
        blur.setInput(inputAllocation)
        blur.forEach(outputAllocation)
        var i = 0
        while (i++ < 8) {
            blur.setInput(outputAllocation)
            blur.forEach(outputAllocation)
        }

        outputAllocation.copyTo(outputBitmap)
        inputBitmap.recycle()
        mRenderScript.destroy()
        return outputBitmap
    }

    public fun addInt() {
        val input1 = longArrayOf(30, 5020)
        val sum1: Int2? = mAddIntScript.reduce_findMinAndMax(input1).get()
        val typeBuilder = Type.Builder(mRenderScript, Element.I64(mRenderScript)).apply {
            //setX(33)
            //setY(55)
        }

        val input2: Allocation = Allocation.createTyped(mRenderScript, typeBuilder.create()).also {
            populateSomeShow(it)
        }
        val result2: ScriptC_addint.result_int2 = mAddIntScript.reduce_findMinAndMax(input2)
        val sum2 = result2.get()
        Log.e("tifone", "sum1 x:${sum1?.x} y:${sum1?.y}")
        Log.e("tifone", "sum1 x:${sum2?.x} y:${sum2?.y}")
    }
    private fun populateSomeShow(input: Allocation) {
        input.copyFrom(longArrayOf(10, 20))
    }
}
