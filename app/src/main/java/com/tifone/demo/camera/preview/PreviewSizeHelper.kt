package com.tifone.demo.camera.preview

import android.util.Size
import android.view.Surface
import com.tifone.demo.camera.device.DeviceInfo
import com.tifone.demo.camera.tlogd
import com.tifone.demo.camera.tloge
import com.tifone.demo.camera.tlogw
import java.util.*

class PreviewSizeHelper {
    companion object {
        const val TAG = "PreviewSizeHelper"
    }
    fun getMatchSize(availableSizes: Array<Size>, targetRatio: Float): Size {
        val screenSize = DeviceInfo.get().getScreenSize()
        tlogd(TAG, "screen size: $screenSize")
        val targetSize = Size((screenSize.width * targetRatio).toInt(), screenSize.width)
        return findBestMatchPreviewSize(availableSizes.toList(), targetSize)
    }

    private fun rotatedPreviewSize(previewSize:Size,
                                      sensorOrientation: Int,
                                      screenRotation: Int): Size {
        val width = previewSize.width
        val height = previewSize.height
        var rotatedWidth = width
        var rotatedHeight = height
        if (needSwapDimensions(sensorOrientation, screenRotation)) {
            rotatedWidth = height
            rotatedHeight = width
        }
        return Size(rotatedWidth, rotatedHeight)
    }

    private fun needSwapDimensions(sensorOrientation: Int,
                                   screenRotation: Int): Boolean {
        tlogd("device rotation = $sensorOrientation")
        var needSwapped = false
        when (screenRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    needSwapped = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    needSwapped = true
                }
            }
            else -> tloge("Screen rotation is invalid: $screenRotation")
        }
        return needSwapped
    }

    private fun findBestMatchPreviewSize(sizes: List<Size>,
                                                defaultDisplaySize: Size): Size {
        var bastMatchIndex = sizes.indexOf(defaultDisplaySize)
        if (bastMatchIndex != -1) {
            return sizes[bastMatchIndex]
        }
        var result = findSizeWithRatio(sizes, defaultDisplaySize)
        tlogd(TAG, "size = $result")
        if (result == null) {
            result = findSizeWithArea(sizes, defaultDisplaySize)
        }
        tlogd(TAG, "previewSize = $result")
        return result
    }

    private fun findSizeWithRatio(sizes: List<Size>, defaultDisplaySize: Size): Size? {
        val targetRatio = getRatio(defaultDisplaySize)
        val matchedTargets = ArrayList<Size>()
        tlogd(TAG, "targetRatio = $targetRatio")
        for (size in sizes) {
            val itsRatio = getRatio(size)
            tlogd(TAG, "itsRatio = $itsRatio")
            // use the display size if matched
            if (size == defaultDisplaySize) {
                return size
            }
            if (isRatioMatched(itsRatio, targetRatio)) {
                // aspect ratio is nearest
                matchedTargets.add(size)
            }
        }
        var result: Size? = null
        if (matchedTargets.size != 0) {
            result = Collections.max(matchedTargets, CompareSizesByArea())
        }
        return result
    }

    private fun findSizeWithArea(sizes: List<Size>, defaultDisplaySize: Size): Size {
        val targetRatio = getRatio(defaultDisplaySize)
        val targetArea = getArea(defaultDisplaySize)
        var minRatioDiff = java.lang.Double.MAX_VALUE
        var minAreaDiff = java.lang.Double.MAX_VALUE
        var targetIndex = -1
        var bigEnoughIndex = -1
        for (i in sizes.indices) {
            val size = sizes[i]
            val ratio = getRatio(size)
            val area = getArea(size)
            if (isBigEnough(size, defaultDisplaySize)) {
                tlogd(TAG, "isBigEnough = $size")
                if (getDiff(targetRatio, ratio) < minRatioDiff) {
                    minRatioDiff = getDiff(targetRatio, ratio)
                    bigEnoughIndex = i
                }
            } else {
                tlogd(TAG, "notBigEnough = $size")
                if (getDiff(targetArea, area) < minAreaDiff) {
                    tlogd(TAG, "isMatched = $size")
                    minAreaDiff = getDiff(targetArea, area)
                    targetIndex = i
                }
            }
        }
        return if (bigEnoughIndex == -1)
            sizes[targetIndex]
        else
            sizes[bigEnoughIndex]
    }

    private fun getArea(target: Size): Double {
        return (target.width * target.height).toDouble()
    }

    private fun getDiff(src: Double, dest: Double): Double {
        return Math.abs(src - dest)
    }

    private fun getRatio(size: Size): Double {
        return size.width.toDouble() / size.height
    }

    private fun isRatioMatched(itsRatio: Double, targetRatio: Double): Boolean {
        return Math.abs(itsRatio - targetRatio) < getTolerance(targetRatio)
    }

    private fun isBigEnough(src: Size, target: Size): Boolean {
        return (src.width >= target.width) and (src.height >= target.height)
    }

    private fun getTolerance(ratio: Double): Double {
        return if (ratio > 1.3433 && ratio < 1.35) {
            tlogw(TAG, "4:3 ratio out of normal tolerance, increasing tolerance to 0.02")
            0.02
        } else {
            0.01
        }
    }
}