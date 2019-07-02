package com.tifone.demo.camera.camera;

import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.util.Size;

import com.tifone.demo.camera.preview.CompareSizesByArea;
import com.tifone.demo.camera.utils.CameraUtil;
import com.tifone.demo.camera.utils.SetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CaptureHelper {

    /**
     * get all output size for the specify format target
     * @param scm
     * @param format format to output
     * @return available output sizes for format
     */
    public static List<Size> getOutputSizes(@NonNull StreamConfigurationMap scm, int format) {
        // get the output size of format
        Size[] normalOutput = scm.getOutputSizes(format);
        // get for high resolution
        Size[] highResolutionOutput= scm.getHighResolutionOutputSizes(format);
        if (highResolutionOutput == null) {
            return Arrays.asList(normalOutput);
        }
        // merge array
        return SetUtil.mergeArrays(highResolutionOutput, normalOutput);
    }
    /**
     * get the size of image which want to save.
     * it will return the of best, largest and match ratio
     * @param availableSizes
     * @param targetAspectRatio the ratio which want to output
     * @return the match size of image
     */
    public static Size getCaptureImageSize(@NonNull List<Size> availableSizes, float targetAspectRatio) {
        List<Size> bestMatch = new ArrayList<>();
        for (Size size : availableSizes) {
            if (Math.abs(CameraUtil.getAspectRatio(size) - targetAspectRatio) < 0.01) {
                bestMatch.add(size);
            }
        }
        if (bestMatch.isEmpty()) {
            return Collections.max(availableSizes, new CompareSizesByArea());
        } else {

            return Collections.max(bestMatch, new CompareSizesByArea());
        }
    }
}
