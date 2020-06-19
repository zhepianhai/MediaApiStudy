package com.zph.media.append.api.codec

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.config.Constants
import com.zph.media.util.AudioCodec
import com.zph.media.util.ToastUtil
import kotlinx.android.synthetic.main.activity_media_codec.*
import kotlinx.android.synthetic.main.layout_navi.*
import java.io.File


/**
 *
 * MediaCodec是Android提供的用于对音视频进行编解码的类，它通过访问底层的codec来实现编解码的功能
 *
 *
 * */
class MediaCodecActivity : BaseActivity() {

    var stringBufferAudio = StringBuffer()

    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, MediaCodecActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_media_codec
    }

    override fun initTopBar() {
        tv_title.text = "MediaCodec使用"
        lay_back.setOnClickListener {
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        ll_audio_codec.visibility = View.GONE
        ll_video_codec.visibility = View.GONE
        rg_codec.setOnCheckedChangeListener { _, checkedId ->
            kotlin.run {
                when (checkedId) {
                    R.id.rb_audio_codec -> {
                        ll_audio_codec.visibility = View.VISIBLE
                        ll_video_codec.visibility = View.GONE
                    }
                    R.id.rb_media_codec -> {
                        ll_audio_codec.visibility = View.GONE
                        ll_video_codec.visibility = View.VISIBLE
                    }
                }
            }
        }
        //将音频文件解码成原始的PCM数据
        btn_getpcm.setOnClickListener {
            stringBufferAudio.setLength(0)//清除
            var tipDialog1 = QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("解码中...").create()
            var tipDialog2 = QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("编码中...").create()


            var mp3SourcePath =
                File(Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/music.mp3")
            if (!mp3SourcePath.exists()) {
                ToastUtil.showToast(this, "原始数据不存在")
            }
            appendAudioCodec("原始MP3文件：" + mp3SourcePath.absoluteFile)

            var pcmSavePath =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_PCM_FILE_PATH

            var saveFile = File(pcmSavePath + "/codec_test.pcm")
            if (saveFile.exists())
                saveFile.delete()
            saveFile.createNewFile()
            appendAudioCodec("解码后pcm路径:" + saveFile.absoluteFile)

            var aacSavePath =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_ACC_FILE_PATH
            var aacFile = File(aacSavePath + "/codec_test.m4a")
            if (aacFile.exists())
                aacFile.delete()
            aacFile.createNewFile()

            appendAudioCodec("编码后aac路径:" + aacFile.absoluteFile)
            tipDialog1.show()
            AudioCodec.getPCMFromAudio(
                mp3SourcePath.absolutePath,
                saveFile.absolutePath,
                object : AudioCodec.AudioDecodeListener {
                    override fun decodeOver() {
                        tipDialog1.dismiss()
                        appendAudioCodec("音频解码完成,开始编码！")
                        tipDialog2.show()
                        AudioCodec.PcmToAudio(
                            saveFile.absolutePath,
                            aacFile.absolutePath,
                            object : AudioCodec.AudioDecodeListener {
                                override fun decodeOver() {
                                    tipDialog2.dismiss()
                                    appendAudioCodec("音频编码完成！")
                                }

                                override fun decodeFail() {
                                    tipDialog2.dismiss()
                                    appendAudioCodec("音频编码失败！")
                                }

                            })

                    }

                    override fun decodeFail() {
                        tipDialog1.dismiss()
                        appendAudioCodec("音频解码失败，不能进行编码！")
                    }

                })
        }
    }

    //用于音频解码编码的文本显示
    fun appendAudioCodec(contetn: String) {
        runOnUiThread {
            stringBufferAudio.append("==$contetn\n==")
            tv_codec_aduio_hind.text = stringBufferAudio.toString()
        }
    }
}
