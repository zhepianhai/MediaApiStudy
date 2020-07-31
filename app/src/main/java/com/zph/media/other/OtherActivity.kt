package com.zph.media.other

import android.app.Activity
import android.content.Intent
import com.zph.media.R
import com.zph.media.base.BaseActivity

class OtherActivity : BaseActivity() {
    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, OtherActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_other
    }

    override fun initTopBar() {

    }



}
