package com.tifone.demo.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tifone.demo.camera.R;
import com.tifone.demo.camera.event.ShutterClickDispatcher;
import com.tifone.demo.camera.thumb.ThumbnailViewController;
import com.tifone.demo.camera.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

public class BottomLayoutManager {
    private Context mContext;
    private ViewGroup mRoot;
    private ImageView mShutterButton;
    private ShutterClickDispatcher mDispatcher;
    private CircleImageView mThumbView;
    private ThumbnailViewController mThumbnailController;

    public BottomLayoutManager(Context context, ViewGroup root) {
        mContext = context;
        mRoot = root;
        mDispatcher = ShutterClickDispatcher.Instance.getDefault();
    }
    public void init() {
        // inflater bottom bar layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_bar_layout, mRoot, false);
        mShutterButton = view.findViewById(R.id.shutter_btn);
        // set shutter listener
        mShutterButton.setOnClickListener(mShutterClickedListener);

        mThumbView = view.findViewById(R.id.thumb_iv);
        mThumbnailController = new ThumbnailViewController(mContext, mThumbView);
        // add layout to root
        mRoot.addView(view);
    }
    private View.OnClickListener mShutterClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDispatcher.onShutterClicked(v);
        }
    };

    public void updateThumb(Bitmap bitmap) {
        mThumbnailController.setThumbnail(bitmap);
    }

    @NotNull
    public Size getThumbSize() {
        return new Size(mThumbView.getWidth(), mThumbView.getHeight());
    }


}
