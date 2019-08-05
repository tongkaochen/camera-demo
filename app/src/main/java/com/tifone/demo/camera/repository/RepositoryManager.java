package com.tifone.demo.camera.repository;

import com.tifone.demo.camera.model.StorageModel;

public class RepositoryManager {

    public static StorageModel getImageRepository() {
        return ImageSaveTask.Companion.getInstance();
    }
}
