package com.zph.media.append.opengles

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.layout_navi.*

/**
 * OpenglEs 绘制三角形
 *
 * */
class TriangleActivity : BaseActivity() {

    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, TriangleActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_triangle
    }

    override fun initTopBar() {
        tv_title.text = "MediaPlay"
        lay_back.setOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

    }
}
