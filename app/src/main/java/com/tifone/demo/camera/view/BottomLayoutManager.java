package com.tifone.demo.camera.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tifone.demo.camera.R;
import com.tifone.demo.camera.event.ShutterClickDispatcher;

public class BottomLayoutManager {
    private Context mContext;
    private ViewGroup mRoot;
    private ImageView mShutterButton;
    private ShutterClickDispatcher mDispatcher;
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
        // add layout to root
        mRoot.addView(view);
    }
    private View.OnClickListener mShutterClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDispatcher.onShutterClicked(v);
        }
    };
}
