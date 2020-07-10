package com.zph.media.append.api.camera2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.zph.media.R
import com.zph.media.append.api.camera2.fragment.Camera2VideoFragment
import com.zph.media.append.api.camera2.fragment.FragmentCamera2NV21
import com.zph.media.append.api.camera2.fragment.FragmentCamera2Video
import com.zph.media.base.BaseActivity


/**
 * 视频采集。使用TextTrueView
 *
 */


open class Camera2Test1Activity : BaseActivity() {
    private val REQUEST_CAMERA_CODE = 100
    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, Camera2Test1Activity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideoView()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_camera2_test1
    }

    override fun initTopBar() {

    }

    private fun initVideoView() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var strings: Array<String> = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(strings, REQUEST_CAMERA_CODE)
            return
        }

        initFrameLayout()

    }

    private fun initFrameLayout() {
        var supportFragmentManager = supportFragmentManager
        var fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, FragmentCamera2NV21.newInstance()).commit()
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initFrameLayout()
            }
        }
    }


}
