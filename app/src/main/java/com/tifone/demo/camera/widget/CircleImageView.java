package com.tifone.demo.camera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tifone.demo.camera.utils.ImageUtil;

/**
 * Create by Tifone on 2019/4/23.
 */
public class CircleImageView extends ImageView {
    private RoundDrawable mRoundDrawable;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        mRoundDrawable = new RoundDrawable();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mRoundDrawable.setBitmap(bm);
        setImageDrawable(mRoundDrawable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRoundDrawable.release();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
