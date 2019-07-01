package com.tifone.demo.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraDemoActivity :  Activity(){
    private var mCameraPreview: CameraPreview? = null
    private var mCamera: Camera? = null
    private var mCameraButton: ImageView? = null;

    companion object {
        const val MEDIA_TYPE_IMAGE: Int = 0
        const val MEDIA_TYPE_VIDEO: Int = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_api1_demo_layout)
        requestPermission()
        initView()
        mCamera = getCameraInstance()
        initCameraPreview()

    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            }
        }
    }
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open()
        } catch (e: IOException) {
            null
        }
    }

    private fun initCameraPreview() {
        mCameraPreview = mCamera?.let {
            CameraPreview(this, it)
        }
        mCameraPreview?.also {
            val preview:FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }
    }

    private fun initView() {
        mCameraButton = findViewById(R.id.capture)
        mCameraButton?.setOnClickListener {
            mCameraButton?.isEnabled = false
            mCamera?.takePicture(null,null,mPicture)
        }
    }
    private fun getOutputMediaFile(type: Int) : File? {
        val mediaStorageDir: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCamera")
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.e("tifone", "Fail to create media directory")
                    return null
                }
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when(type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.parent}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.parent}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }

    private val mPicture = Camera.PictureCallback { data, camera ->
        mCameraButton?.isEnabled = true
        mCamera?.startPreview()
        val pictureFile: File = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            return@PictureCallback
        }
        Log.e("tifone", pictureFile.toString())
        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {

        } catch (e: IOException) {

        }
    }
}