package com.tifone.demo.camera.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.widget.ImageView
import com.tifone.demo.camera.R
import com.tifone.demo.camera.device.DeviceInfo
import com.tifone.demo.camera.presenter.IPresenter
import com.tifone.demo.camera.presenter.TexturePresenter
import com.tifone.demo.camera.utils.PermissionUtil
import kotlinx.android.synthetic.main.camera_main_layout.*

class CameraActivity : IView, Activity() {
    private lateinit var itsPresenter: IPresenter<SurfaceTexture>
    private lateinit var mPreviewView: SurfaceView
    private lateinit var mCaptureButton: ImageView
    private lateinit var mTexture: TextureView
    private val mDeviceInfo = DeviceInfo.get()

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
                //itsPresenter = CameraPresenter(this@CameraActivity, holder)
                //itsPresenter.requestCamera()
            }

        })
        mTexture.surfaceTextureListener = object :TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return false
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                surface.setDefaultBufferSize(640, 480)
                itsPresenter = TexturePresenter(this@CameraActivity, surface)
                itsPresenter.requestCamera(surface)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            }
        }
    }
    private fun checkPermissionAndGrant() {
        PermissionUtil.checkCameraPermission(this)
        PermissionUtil.checkStoragePermission(this)
    }
    private fun initView() {
        // kotlin extension
//        mPreviewView = camera_preview
//        mCaptureButton = capture
//        mTexture = camera_texture
    }

    override fun getContext(): Context = this

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mDeviceInfo.updateDisplayInfo(windowManager.defaultDisplay)
    }
}