package com.zph.media

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.zph.media.append.api.AndroidAudioApiActivity
import com.zph.media.append.api.AndroidMediaApiActivity
import com.zph.media.append.api.MediaExtractorActivity
import com.zph.media.append.api.MediaMuxerActivity
import com.zph.media.append.api.audio.AudioWaveformActivity
import com.zph.media.append.api.camera2.Camera2Test1Activity
import com.zph.media.append.api.codec.MediaCodecActivity
import com.zph.media.append.opengles.TriangleActivity
import com.zph.media.base.BaseActivity
import com.zph.media.config.Constants
import com.zph.media.home.adapter.AdapterGridHome
import com.zph.media.util.FileUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : BaseActivity() {

    private var adapter: AdapterGridHome? = null
    private val mData: MutableList<String> = arrayListOf()

    companion object {

        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initTopBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFile()
        initGridView()

    }

    private fun initFile() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            //文件创建
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_IMAGE_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_AUDIO_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_ACC_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_H246_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_MP4_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_PCM_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_TEMP_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_OTHER_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_CACHE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_WAV_FILE_PATH)
            FileUtil.create(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_IFLY_WAV_FILE_PATH)
            //测试数据的视频源文件
            var fileTestMp4 =
                File(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/test.mp4")
            if (!fileTestMp4.exists()) {
                FileUtil.copyFilesFromRaw(
                    this,
                    R.raw.test,
                    "test.mp4",
                    getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH
                )
            }
            //测试数据的音频源文件
            var fileTestMp3 =
                File(getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/music.mp3")
            if (!fileTestMp3.exists()) {
                FileUtil.copyFilesFromRaw(
                    this,
                    R.raw.music,
                    "music.mp3",
                    getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH
                )
            }
        } else {
            Log.i("TAGG", "文件创建失败")
        }

    }

    private fun initGridView() {
        mData.add(0, "Media")
        mData.add(1, "PCM")
        mData.add(2, "MediaExtractor")
        mData.add(3, "MediaMuxer")
        mData.add(4, "MediaCodec")
        mData.add(5, "H.264")
        mData.add(6, "YUV")
        mData.add(7, "IPB帧")
        mData.add(8, "GL_三角形")
        mData.add(9, "音频波形")
        adapter = AdapterGridHome(this, mData)
        gridview_home.adapter = adapter
        gridview_home.setOnItemClickListener { parent, view, position, id ->
            run {
                when (position) {
                    0 -> {
                        Camera2Test1Activity.openActivity(this)
//                        AndroidMediaApiActivity.openActivity(this)
                    }
                    1 -> {
                        AndroidAudioApiActivity.openActivity(this)
                    }
                    2 -> {
                        MediaExtractorActivity.openActivity(this)
                    }
                    3 -> {
                        MediaMuxerActivity.openActivity(this)
                    }
                    4 -> {
                        MediaCodecActivity.openActivity(this)
                    }
                    8 -> {
                        TriangleActivity.openActivity(this)
                    }
                    9->{
                        AudioWaveformActivity.openActivity(this)
                    }
                    else -> {

                    }
                }
            }
        }
    }


}
