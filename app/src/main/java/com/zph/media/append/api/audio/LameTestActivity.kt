package com.zph.media.append.api.audio

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.util.ZPHLameUtils
import kotlinx.android.synthetic.main.activity_lame_test.*
import kotlinx.android.synthetic.main.layout_navi.*
import java.lang.Exception

class LameTestActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_lame_test
    }

    override fun initTopBar() {
        tv_title.text = "LameTest"
        lay_back.setOnClickListener {
            finish()
        }
    }

    init {
        try {
            System.loadLibrary("lame-lib")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, LameTestActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showTestLameVersion()
    }

    private fun showTestLameVersion() {
        tvLameTest.text = "当前Lame的版本号是："+ZPHLameUtils.getLameVersion()
    }

}
