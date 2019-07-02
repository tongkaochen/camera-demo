package com.tifone.demo.camera.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import static com.tifone.demo.camera.LogUtilKt.logd;
import static com.tifone.demo.camera.LogUtilKt.loge;


public class AutoFillTextureView extends TextureView {
    private float mAspectRatio;
    public AutoFillTextureView(Context context) {
        super(context);
    }

    public AutoFillTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFillTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio < 0) {
            throw new IllegalArgumentException("aspectRatio >= 0");
        }
        logd(this, "aspectRatio is " + aspectRatio);
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setDimensionWithAspectRatio(width, height);
    }
    private void setDimensionWithAspectRatio(int width, int height) {
        if (mAspectRatio <= 0) {
            setMeasuredDimension(width, height);
        } else {
            // preview size ratio is larger than 1.0f,
            // mean the texture view height's width < height
            if (mAspectRatio > 1.0f) {
                // need texture' s width < height
                if (width > height) {
                    // height can be match, width should be smaller than height
                    setMeasuredDimension((int) (height / mAspectRatio), height);
                } else {
                    // width can be match, height should be larger than width
                    setMeasuredDimension(width, (int) (width * mAspectRatio));
                }
            } else {
                // need texture' width > height
                if (width > height) {
                    // height can be match, width should be smaller than height
                    setMeasuredDimension((int) (height * mAspectRatio), height);
                } else {
                    // width can be match, height should be larger than width
                    setMeasuredDimension(width, (int) (width / mAspectRatio));
                }
            }
        }
    }

}
