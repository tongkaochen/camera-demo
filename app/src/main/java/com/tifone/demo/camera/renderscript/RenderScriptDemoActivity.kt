package com.tifone.demo.camera.renderscript

import android.app.Activity
import android.os.Bundle
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageView
import com.tifone.demo.camera.R

class RenderScriptDemoActivity : Activity(){
    private var mOriginImg: ImageView? = null
    private var mRenderImg: TouchableImageView? = null

    private lateinit var mUtil: RsUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.render_demo_layout)
        mOriginImg = findViewById(R.id.origin_img)
        mRenderImg = findViewById(R.id.render_img)
        mUtil = RsUtil(this)
        mRenderImg?.setRsUtil(mUtil)
        mRenderImg?.setImageBitmap(mUtil.render(0.5f, 0.5f))
        mUtil.addInt()
    }
}