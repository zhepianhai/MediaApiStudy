package com.zph.media.append.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.zph.media.MainActivity
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.util.ToastUtil
import java.util.*

class SplashActivity : BaseActivity() {

    private val PERMISSION = 1
    private val FLAG_WHAT=2
    private var permission = false
    private var handler:Handler= Handler{
        when(it.what){
            FLAG_WHAT->{
                intoMainActivity()
            }
        }
        false
    }



    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initTopBar() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        getPermisions()

    }

    private fun getPermisions() {
        val permissions = ArrayList<String>()
        /***
         * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
         */
        // 定位精确位置
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) !== PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CHANGE_NETWORK_STATE)
        }
        if (permissions.size > 0) {
            requestPermissions(
                permissions.toTypedArray(),
                PERMISSION
            )
        } else {
            permission = true
            defaultLoadMain()
        }
    }

    private fun defaultLoadMain() {
       handler.sendEmptyMessageDelayed(FLAG_WHAT,2000)
    }
    private fun intoMainActivity(){
        MainActivity.openActivity(this)
        finish()
    }

    override fun onDestroy() {
        handler!!.removeMessages(FLAG_WHAT)
        super.onDestroy()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    defaultLoadMain()
                } else {
                    ToastUtil.showToast(this@SplashActivity, "拒绝相关权限将无法使用")
                }
        }
    }


}
