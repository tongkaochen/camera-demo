package com.tct.magnifier.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.tct.magnifier.LogUtil;
import com.tct.magnifier.camera.CameraSettings;
import com.tct.magnifier.device.DeviceSettings;

import static com.tct.magnifier.LogUtil.logd;
import static com.tct.magnifier.LogUtil.loge;

public class AutoFillTextureView extends TextureView implements ViewAdjustment {
    private int mRatioWidth;
    private int mRatioHeight;
    public AutoFillTextureView(Context context) {
        super(context);
    }

    public AutoFillTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFillTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width and height should >= 0");
        }
        logd(this, "setAspectRatio : width = " + width + " height " + height);
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }
    public void setDefaultBufferSize(int width, int height) {
        LogUtil.logd("setDefaultBufferSize: width = " + width
                + " height = " + height);
        getSurfaceTexture().setDefaultBufferSize(width, height);
    }

    // TODO, maybe should remove
    private void configureTransform() {
        loge(this, "configureTransform");
        int rotation = DeviceSettings.getInstance().rotation;
        Size previewSize = CameraSettings.getInstance().previewSize;
        int previewWidth = previewSize.getWidth();
        int previewHeight = previewSize.getHeight();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        loge(this, "width = " + viewWidth + ", height = " + viewHeight);
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewHeight, previewWidth);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewHeight,
                    (float) viewWidth / previewWidth);
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        setTransform(matrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setDimensionWithAspectRatio(width, height);
    }
    private void setDimensionWithAspectRatio(int width, int height) {
        if (mRatioWidth == 0 || mRatioHeight == 0) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                // width is smaller
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                // height is smaller
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}
