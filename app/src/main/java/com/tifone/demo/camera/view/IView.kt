package com.tifone.demo.camera.view

import android.content.Context

interface IView {
    fun getContext(): Context
    fun getViewState(): ViewState
}