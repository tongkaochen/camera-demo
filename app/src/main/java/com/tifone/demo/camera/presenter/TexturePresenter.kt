package com.tifone.demo.camera.presenter

import android.app.Activity
import android.graphics.SurfaceTexture
import android.view.Surface
import com.tifone.demo.camera.agent.CameraAgent
import com.tifone.demo.camera.agent.CameraOperationsImpl

class TexturePresenter(activity: Activity, surfaceTexture: SurfaceTexture): IPresenter<SurfaceTexture> {
    private val mActivity = activity
    private var mCameraAgent: CameraAgent<SurfaceTexture> = CameraAgent(
            CameraOperationsImpl(activity))
    override fun requestCamera(surfaceTexture: SurfaceTexture) {
        mCameraAgent.openCamera(surfaceTexture)
    }
}