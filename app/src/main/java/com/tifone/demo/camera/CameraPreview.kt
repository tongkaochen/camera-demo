package com.tifone.demo.camera

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(context:Context,
                    private val mCamera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    private val mHolder: SurfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mCamera.apply {
            try {
                setPreviewDisplay(holder)
                setDisplayOrientation(90)
                startPreview()
            } catch (e: IOException) {
                Log.e("tifone", "error settings camera preview, error : ${e.message}")
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (holder.surface == null) {
            return
        }
        try {
            mCamera.stopPreview()

        }catch (e: IOException) {

        }
        mCamera.apply {
            try {

                setPreviewDisplay(holder)
                startFaceDetection()
                startPreview()
            } catch (e: IOException) {
                Log.e("tifone", "error settings camera preview, error : ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasure: Int = MeasureSpec.getSize(widthMeasureSpec) * 4 / 3
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightMeasure)
    }
}