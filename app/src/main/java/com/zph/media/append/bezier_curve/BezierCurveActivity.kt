package com.zph.media.append.bezier_curve

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.layout_navi.*

/**
 * 贝塞尔曲线
 * */
class BezierCurveActivity : BaseActivity() {
    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, BezierCurveActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bezier_curve
    }

    override fun initTopBar() {
        tv_title.text = "贝塞尔曲线"
        lay_back.setOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }
    private fun initView(){

    }


}
