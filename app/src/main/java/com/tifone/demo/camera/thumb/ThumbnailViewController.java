package com.tifone.demo.camera.thumb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ThumbnailViewController {
    private ImageView mThumbView;
    private ThumbnailModel mThumbnailModel;
    private Context mContext;

    public ThumbnailViewController(Context context, ImageView thumb) {
        if (thumb == null) {
            new IllegalArgumentException("thumb should be null");
        }
        mContext = context;
        mThumbView = thumb;
        mThumbnailModel = new ThumbnailModelImpl();
        tryToGetThumbnailFromMedia();


    }

    @SuppressLint("CheckResult")
    private void tryToGetThumbnailFromMedia() {
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = mThumbnailModel.getThumbnail(
                        mContext.getApplicationContext());
                emitter.onNext(bitmap);
                emitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        setThumbnail(bitmap);
                    }
                });
    }

    public void setThumbnail(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mThumbView.setImageBitmap(bitmap);
    }
}
