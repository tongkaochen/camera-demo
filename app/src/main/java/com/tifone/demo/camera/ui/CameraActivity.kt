package com.tifone.demo.camera.ui

import android.app.Activity
import android.os.Bundle
import com.tifone.demo.camera.R
import com.tifone.demo.camera.presenter.CameraPresenter

class CameraActivity: Activity() {
    private lateinit var mPresenter: CameraPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_main_layout)
        initView()

    }
    private fun initView() {

    }
}