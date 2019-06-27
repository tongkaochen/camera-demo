package com.tifone.demo.camera.model

import android.content.Context
import com.tifone.demo.camera.stragety.ApiLevel
import com.tifone.demo.camera.stragety.CameraApiStrategy

class CameraModelManager {
    companion object {
        fun createCameraModel(context: Context, apiStrategy: CameraApiStrategy)
                : BaseCameraModel {
            return when (apiStrategy.getApi()) {
                ApiLevel.API1 -> createCameraApi1Model(context)
                ApiLevel.API2 -> createCameraApi2Model(context)
            }
        }
        fun createCameraModel(context: Context, apiLevel: ApiLevel): BaseCameraModel {
            return when (apiLevel) {
                ApiLevel.API1 -> createCameraApi1Model(context)
                ApiLevel.API2 -> createCameraApi2Model(context)
            }
        }

        private fun createCameraApi1Model(context: Context): BaseCameraModel {
            return CameraModel(context)
        }

        private fun createCameraApi2Model(context: Context): BaseCameraModel {
            return Camera2Model(context)
        }
    }
}