package com.zph.media.append.ndk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.util.NDKTestUtils
import kotlinx.android.synthetic.main.activity_ndk_test1.*
import kotlinx.android.synthetic.main.layout_navi.*
import java.lang.Exception

class NdkTest1Activity : BaseActivity() {
    companion object {

        fun openActivity(activity: Activity) {
            val intent = Intent(activity, NdkTest1Activity::class.java)
            activity.startActivity(intent)
        }
    }

    init {
        try {
            System.loadLibrary("lame-lib")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ndk_test1;
    }

    override fun initTopBar() {
        tv_title.text = "NdkTest1"
        lay_back.setOnClickListener {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showTestNdkString()
    }

    @SuppressLint("SetTextI18n")
    private fun showTestNdkString() {
        //拼接
        tvNdk1.text="拼接JString："+NDKTestUtils.operateString("java")
        //数组求和
        var array= IntArray(3)
        array[0]=1
        array[1]=2
        array[2]=3
        tvNdk2.text="数组求和："+array.asList().toString()+"结果："+NDKTestUtils.sumArray(array)

    }

}
