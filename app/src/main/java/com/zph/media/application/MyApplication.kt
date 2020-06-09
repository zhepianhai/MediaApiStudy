package com.zph.media.application

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager

class MyApplication : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        QMUISwipeBackActivityManager.init(this)
//        DoraemonKit.install(this,null,"pId");
//        DoraemonKit.install(this,"41b8dc9c2837c4287c6a17c60754177d")
    }

}