package com.tifone.demo.camera.ui

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.tifone.demo.camera.R
import com.tifone.demo.camera.camera.CameraSettings
import com.tifone.demo.camera.device.DeviceInfo
import com.tifone.demo.camera.tlogd
import com.tifone.demo.camera.module.ModuleID
import com.tifone.demo.camera.module.ModuleManager
import com.tifone.demo.camera.preview.AutoFillTextureView
import com.tifone.demo.camera.preview.TextureViewController
import com.tifone.demo.camera.utils.PermissionUtil
import com.tifone.demo.camera.view.BottomLayoutManager
import com.tifone.demo.camera.view.CameraUI
import com.tifone.demo.camera.view.ViewState

class CameraActivity: BaseActivity(), CameraUI {
    object RequestPermissions {
        val PERMISSIONS:Array<String> = arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    companion object {
        const val REQUEST_PERMISSION_CODE = 1000
    }

    private lateinit var mBottomBarContainer: FrameLayout
    private lateinit var mTextureViewController: TextureViewController
    private lateinit var mPreviewContainer: FrameLayout
    private lateinit var mModuleManager: ModuleManager
    private val mDeviceInfo = DeviceInfo.get()
    private var mViewState = ViewState.DESTROYED
    private lateinit var mBottomLayoutManager: BottomLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        setContentView(R.layout.camera_main_layout)
        initView()
        initSurfaceHolder()
        initModule()
    }
    private fun checkAndRequestPermissions() {
        // request camera and storage permission
        // check if grand camera and storage permission
        var granted = true
        for (permission in RequestPermissions.PERMISSIONS) {
            if (!PermissionUtil.isPermissionGranted(this, permission)) {
                granted = false
                break
            }
        }
        if (granted) {
            return
        }
        requestPermissions(RequestPermissions.PERMISSIONS, REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when(requestCode) {
            REQUEST_PERMISSION_CODE -> {
                var granted = permissions.isNotEmpty()
                for (permission in permissions) {
                    if (!PermissionUtil.isPermissionGranted(this, permission)) {
                        granted = false
                        break
                    }
                }
                if (granted) {
                    mModuleManager.startModule()
                }
            }
        }
    }

    private fun initView() {
        mPreviewContainer = fView(R.id.preview_container)
        mDeviceInfo.updateDisplayInfo(windowManager.defaultDisplay)
        mBottomBarContainer = fView(R.id.bottom_bar_container)
        val params = mBottomBarContainer.layoutParams as RelativeLayout.LayoutParams
        params.bottomMargin = params.bottomMargin + mDeviceInfo.getNavigationBarHeight()
        mBottomBarContainer.layoutParams = params
        mBottomLayoutManager = BottomLayoutManager(this, mBottomBarContainer)
        mBottomLayoutManager.init()
        tlogd("current thread: ${Thread.currentThread()}")
    }
    private fun initSurfaceHolder() {
        logd("create texture view")
        mTextureViewController = TextureViewController()
        // inflater texture view from layout
        val view = LayoutInflater.from(this).inflate(R.layout.texture_view, null)
        val textureView: AutoFillTextureView = view.findViewById(R.id.preview_texture)
        textureView.setAspectRatio(getUIAspectRatio())
        // add to preview container
        mPreviewContainer.addView(textureView)
        // attach to view holder
        mTextureViewController.attachTextureView(textureView)
    }
    private fun initModule() {
        // create the default module, photo module
        mModuleManager = ModuleManager.getInstance()
        mModuleManager.initModule(this, ModuleID.PHOTO)
        mModuleManager.startModule()
    }

    override fun onResume() {
        super.onResume()
        logd("onResume")
        mViewState = ViewState.DISPLAYING
        hideNavigationBar()
        mModuleManager.startModule()
    }

    private fun hideNavigationBar() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }

    override fun onPause() {
        super.onPause()
        logd("onPause")
        mViewState = ViewState.PAUSED
        mModuleManager.stopModule()
    }

    override fun onStop() {
        super.onStop()
        mViewState = ViewState.STOPPED
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewState = ViewState.DESTROYED
        mModuleManager.destroyModule()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mDeviceInfo.updateDisplayInfo(windowManager.defaultDisplay)
    }

    override fun getContext(): Context {
        return this
    }

    override fun updateShutterStatus(status: Int) {

    }

    override fun getPreviewSurfaceHolder(): TextureViewController {
        return mTextureViewController
    }

    override fun getViewState(): ViewState {
        return mViewState
    }

    override fun getUIAspectRatio(): Float {
        return CameraSettings.ASPECT_RATIO_2_1
    }

    override fun setPreviewAspectRatio(ratio: Float) {
        val textureView: TextureView =
                mTextureViewController.getTextureView()
        if (textureView is AutoFillTextureView) {
            textureView.setAspectRatio(ratio)
        }
    }

    override fun getThumbSize(): Size {
        return mBottomLayoutManager.thumbSize
    }

    override fun updateThumb(bitmap: Bitmap) {
        runOnUiThread {
            mBottomLayoutManager.updateThumb(bitmap)
        }
    }

    private fun logd(msg: String) {
        tlogd(this, msg)
    }
}