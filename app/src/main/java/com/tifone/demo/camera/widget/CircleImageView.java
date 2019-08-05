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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.tifone.demo.camera.R;
import com.tifone.demo.camera.utils.ImageUtil;

/**
 * Create by Tifone on 2019/4/23.
 */
public class CircleImageView extends ImageView {
    private RoundDrawable mRoundDrawable;
    private Paint mPaint;
    private float mRadius;
    private static final int STROKE_WIDTH = 4;

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
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null) {
            return;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        // crop the center of bitmap
        if (width > height) {
            int left = (width - height) / 2;
            bm = Bitmap.createBitmap(bm, left, 0, height, height, null, false);
        } else if (width < height){
            int top = (height - width) / 2;
            bm = Bitmap.createBitmap(bm, 0, top, width, width, null, false);
        }
        mRoundDrawable.setBitmap(bm);
        setImageDrawable(mRoundDrawable);
        Animation animation = AnimationUtils.loadAnimation(
                getContext(), R.anim.anim_alpha_thumb);
        startAnimation(animation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRoundDrawable.release();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w, h) - STROKE_WIDTH) / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRoundDrawable.getBitmap() != null) {
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mPaint);
        }
    }

}
