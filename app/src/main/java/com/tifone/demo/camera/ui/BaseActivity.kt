package com.tifone.demo.camera.ui

import android.app.Activity
import android.os.Bundle

abstract class BaseActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }
    protected abstract fun initView()
}