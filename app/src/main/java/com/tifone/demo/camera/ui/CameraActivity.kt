package com.tifone.demo.camera.ui

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import com.tifone.demo.camera.R
import com.tifone.demo.camera.device.DeviceInfo
import com.tifone.demo.camera.presenter.CameraPresenter

class CameraActivity: Activity() {
    private lateinit var mPresenter: CameraPresenter
    private val mDeviceInfo = DeviceInfo.get()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_main_layout)
        initView()

    }
    private fun initView() {

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mDeviceInfo.updateDisplayInfo(windowManager.defaultDisplay)
    }
}