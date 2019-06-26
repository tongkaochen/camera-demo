package com.tifone.demo.camera.model

import com.tifone.demo.camera.stragety.ApiLevel
import com.tifone.demo.camera.stragety.CameraApiStrategy

class CameraModelManager {
    companion object {
        fun getCameraModel(apiStrategy: CameraApiStrategy): BaseCameraModel {
            return when (apiStrategy.getApi()) {
                ApiLevel.API1 -> getCameraApi1Model()
                ApiLevel.API2 -> getCameraApi2Model()
            }
        }
        fun getCameraModel(apiLevel: ApiLevel): BaseCameraModel {
            return when (apiLevel) {
                ApiLevel.API1 -> getCameraApi1Model()
                ApiLevel.API2 -> getCameraApi2Model()
            }
        }

        private fun getCameraApi1Model(): BaseCameraModel {
            return CameraModel.get()
        }

        private fun getCameraApi2Model(): BaseCameraModel {
            return Camera2Model.get()
        }
    }
}