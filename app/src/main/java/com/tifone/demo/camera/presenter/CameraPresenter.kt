package com.tifone.demo.camera.presenter

import android.content.Context
import android.hardware.camera2.CameraManager
import android.view.SurfaceHolder
import com.tifone.demo.camera.agent.CameraAgent
import com.tifone.demo.camera.agent.CameraOperationsImpl
import com.tifone.demo.camera.view.IView

class CameraPresenter(view: IView, holder: SurfaceHolder) :
        IPresenter {
    private var itsView: IView = view
    private val mContext: Context = view.getContext()
    private var mCameraAgent: CameraAgent = CameraAgent(
            CameraOperationsImpl(mContext, holder.surface))
    override fun requestCamera() {
        mCameraAgent.openCamera()
    }
}