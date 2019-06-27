package com.tifone.demo.camera.repository

import com.tifone.demo.camera.utils.DataWrapper

class RepositoryKeys {
    companion object {
        // use to pass the filename which want save something to
        val SAVE_PATH = DataWrapper.Key("save_path", String::class.java)
    }
}