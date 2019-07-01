package com.tifone.demo.camera.ui

import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.View

abstract class BaseActivity: Activity() {
    protected fun <T : View> fView(@IdRes viewId: Int): T {
        return findViewById(viewId)
    }
}