package com.tifone.demo.camera.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageView
import com.tifone.demo.camera.R
import com.tifone.demo.camera.presenter.CameraPresenter
import com.tifone.demo.camera.presenter.IPresenter
import com.tifone.demo.camera.utils.PermissionUtil
import kotlinx.android.synthetic.main.camera_main_layout.*

class CameraActivity : IView, Activity() {
    private lateinit var itsPresenter: IPresenter
    private lateinit var mPreviewView: SurfaceView
    private lateinit var mCaptureButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionAndGrant()
        setContentView(R.layout.camera_main_layout)
        initView()
        mPreviewView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                itsPresenter = CameraPresenter(this@CameraActivity, holder)
                itsPresenter.requestCamera()
            }

        })

    }
    private fun checkPermissionAndGrant() {
        PermissionUtil.checkCameraPermission(this)
        PermissionUtil.checkStoragePermission(this)
    }
    private fun initView() {
        // kotlin extension
        mPreviewView = camera_preview
        mCaptureButton = capture
    }

    override fun getContext(): Context = this
}