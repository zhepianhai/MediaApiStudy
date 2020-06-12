package com.zph.media.base

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import butterknife.ButterKnife
import com.qmuiteam.qmui.arch.QMUIActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper


abstract class BaseActivity : QMUIActivity() {

    abstract fun getLayoutId(): Int
    abstract fun initTopBar()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initQMUIStatusBarHelper()
        initTopBar()
    }

    private fun initQMUIStatusBarHelper() {
        QMUIStatusBarHelper.translucent(this)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
    }
}