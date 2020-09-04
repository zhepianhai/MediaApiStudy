package com.zph.media.append.gpuimage

import android.app.Activity
import android.content.Intent
import android.view.View
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.activity_gpu_image_test.*
import kotlinx.android.synthetic.main.layout_navi.*

class GpuImageTestActivity : BaseActivity() {

    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, GpuImageTestActivity::class.java)
            activity.startActivity(intent)
        }
    }
    init {
        System.loadLibrary("native-lib");
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_gpu_image_test
    }

    override fun initTopBar() {
        tv_title.text = "GpuImage学习"
        lay_back.setOnClickListener {
            finish()
        }
        initView()
    }

    private fun initView() {

        findViewById<View>(R.id.button_gallery).setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }

        findViewById<View>(R.id.button_camera).setOnClickListener {
            startActivity(Intent(this, GupCameraActivity::class.java))
        }

        tv_ffmpeg_version.text=getFFmpegVersion()
    }

    /**
     * @return 返回当前
     */
    private external fun getFFmpegVersion(): String
}
