package com.zph.media.append.api

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.layout_navi.*
/**
 *
 * 在Android中，可以使用MediaMuxer来封装编码后的视频流和音频流到mp4容器中：
 * */
class MediaMuxerActivity : BaseActivity() {

    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, MediaMuxerActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_media_muxer
    }

    override fun initTopBar() {
        tv_title.text = "MediaExtractor"
        lay_back.setOnClickListener {
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
