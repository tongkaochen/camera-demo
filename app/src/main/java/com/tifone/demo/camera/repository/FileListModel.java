package com.tifone.demo.camera.repository;

import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;

public class FileListModel {
    private static FileListModel INSTANCE;
    private FileListModel() {

    }
    public FileListModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileListModel();
        }
        return INSTANCE;
    }

    public String getLatestCapturedImageName(String directory) {
        String result = "";
        if (TextUtils.isEmpty(directory)) {
            return result;
        }
        File targetDir = new File(directory);
        if (!targetDir.isDirectory()) {
            // the target dir is not directory
            return result;
        }
        // list directory file
        // filter the jpg file
        File[] jpgFiles = targetDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.toString().endsWith(".jpg");
            }
        });
        if (jpgFiles == null) {
            return result;
        }
        // get the latest one, format to file name, return
        int latestOneIndex = 0;
        long maxLastModified = jpgFiles[0].lastModified();
        for (int i = 1; i < jpgFiles.length; i++) {
            long lastModified = jpgFiles[i].lastModified();
            if (lastModified > maxLastModified) {
                latestOneIndex = i;
            }
        }
        result = jpgFiles[latestOneIndex].toString();
        return result;
    }
}
