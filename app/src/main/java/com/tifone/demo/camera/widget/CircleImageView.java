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
    private Paint mPaint;
    private PorterDuffXfermode mMode;
    private final int strokeWidth = 6;
    private Drawable mOriginDrawable;
    private Bitmap mSrcBitmap;
    private boolean isInternalCall;

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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mMode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(0);
    }

    @Override
    public Drawable getDrawable() {
        return mOriginDrawable;
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (isInternalCall) {
            isInternalCall = false;
            return;
        }
        mOriginDrawable = drawable;
        clipDrawable();
    }

    private void clipDrawable() {
        Bitmap bitmap = ImageUtil.Companion.translateDrawableToBitmap(mOriginDrawable);
        if (bitmap == null) {
            return;
        }
        bitmap = clipToRoundBitmap(bitmap);
        mSrcBitmap = bitmap;
        isInternalCall = true;
        setImageBitmap(bitmap);
    }
    private void drawableScaleToMatchParent() {
        if (mSrcBitmap == null) {
            return;
        }
        int width = mSrcBitmap.getWidth();
        int height = mSrcBitmap.getHeight();

        // scale
        float widthRatio = (float) getWidth() / width;
        float heightRatio = (float) getHeight() / height;

        float maxRatio = Math.max(widthRatio, heightRatio);
        Matrix matrix = getImageMatrix();
        matrix.reset();
        int diffWidth = getWidth() - width;
        int diffHeight = getHeight() - height;
        matrix.postScale(maxRatio, maxRatio, width / 2.0f, height / 2.0f);
        matrix.postTranslate(diffWidth / 2.0f, diffHeight / 2.0f);
        setImageMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawableScaleToMatchParent();
        super.onDraw(canvas);
        float radius = getWidth() / 2.0f - strokeWidth / 2.0f;
        //canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, radius, mPaint);
    }

    public Bitmap clipToRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int squareWidth = Math.min(width, height);
        float radius = squareWidth / 2.0f;
        Bitmap output = Bitmap.createBitmap(
                squareWidth, squareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        int srcLeft = 0;
        int srcRight = squareWidth;
        int srcTop = 0;
        int srcBottom = squareWidth;
        float diff = Math.abs(width - height) / 2.0f;
        if (width > height) {
            srcLeft = (int) diff;
            srcRight = srcLeft + squareWidth;
        } else {
            srcTop = (int) diff;
            srcBottom = srcTop + squareWidth;
        }
        final Rect src = new Rect(srcLeft , srcTop, srcRight, srcBottom);
        final Rect dst = new Rect(0, 0, squareWidth, squareWidth);

        paint.setAntiAlias(true);
        canvas.drawCircle(squareWidth / 2.0f, squareWidth / 2.0f, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        return output;
    }

}
