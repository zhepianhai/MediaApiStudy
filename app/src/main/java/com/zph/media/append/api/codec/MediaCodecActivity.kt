package com.zph.media.append.api.codec

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.qmuiteam.qmui.widget.tab.QMUITab
import com.qmuiteam.qmui.widget.tab.QMUITabBuilder
import com.qmuiteam.qmui.widget.tab.QMUITabSegment
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.activity_media_codec.*
import kotlinx.android.synthetic.main.layout_navi.*


class MediaCodecActivity : BaseActivity() {


    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, MediaCodecActivity::class.java)
            activity.startActivity(intent)
        }
    }
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
        ll_audio_codec.visibility=View.GONE
        ll_video_codec.visibility=View.GONE
        rg_codec.setOnCheckedChangeListener { _, checkedId ->
            kotlin.run { when(checkedId){
                R.id.rb_audio_codec->{
                    ll_audio_codec.visibility=View.VISIBLE
                    ll_video_codec.visibility=View.GONE
                }
                R.id.rb_media_codec->{
                    ll_audio_codec.visibility=View.GONE
                    ll_video_codec.visibility=View.VISIBLE
                }
            } }
        }
    }
}
