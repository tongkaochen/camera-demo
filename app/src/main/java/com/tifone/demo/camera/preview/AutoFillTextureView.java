package com.tifone.demo.camera.preview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import static com.tifone.demo.camera.LogUtilKt.logd;
import static com.tifone.demo.camera.LogUtilKt.loge;


public class AutoFillTextureView extends TextureView {
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
        logd("setDefaultBufferSize: width = " + width
                + " height = " + height);
        getSurfaceTexture().setDefaultBufferSize(width, height);
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
