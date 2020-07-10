package com.zph.media.application

import android.R
import androidx.multidex.MultiDexApplication
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager


class MyApplication : MultiDexApplication(){
    override fun onCreate() {
        initSpeech()
        super.onCreate()
        QMUISwipeBackActivityManager.init(this)


    }
    private fun initSpeech(){
        val param = StringBuffer()
        param.append("appid=58367e1d")
        param.append(",")
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC)
        SpeechUtility.createUtility(this@MyApplication, param.toString())
    }

}