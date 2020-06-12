package com.zph.media.append.api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.layout_navi.*

class MediaCodecActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_media_codec
    }

    override fun initTopBar() {
        tv_title.text = "MediaCodec使用"
        lay_back.setOnClickListener {
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {

    }
}
