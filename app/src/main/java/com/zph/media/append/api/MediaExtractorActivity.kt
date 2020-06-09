package com.zph.media.append.api

import android.app.Activity
import android.content.Intent
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.config.Constants
import kotlinx.android.synthetic.main.activity_media_extractor.*
import kotlinx.android.synthetic.main.layout_navi.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * Android音频学习之MediaExtractor，提取音频视频轨道数据（从视频中分离音频视频数据）
 * MediaExtractor从api16开始添加，可用于分离视频文件的音轨和视频轨道，
 * 如果你只想要视频，那么用selectTrack方法选中视频轨道，然后用readSampleData读出数据，
 * 这样你就得到了一个没有声音的视频，想得到音频也可以用同样的方法。
 * */
class MediaExtractorActivity : BaseActivity() {

    var mediaExtractor = MediaExtractor()


    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, MediaExtractorActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_media_extractor
    }

    override fun initTopBar() {
        tv_title.text = "MediaExtractor"
        lay_back.setOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        btn_extractor.setOnClickListener {
            getAcc_H264_FromMp4()
        }

    }


    /**
     *
     * 从mp4文件中获取信息并提取音频文件和视频文件
     * 从mp4文件中提取音频和视频轨道的数据，得到aac音频数据和.h264视频数据。
     * */
    private fun getAcc_H264_FromMp4() {
        tv_extractor.text=""
        var stringBuffer = StringBuffer()
        try {
            var pcmPath =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_ACC_FILE_PATH + "/test.aac"
            var mp4Path =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_H246_FILE_PATH + "/test.h264"
            var srcPath =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/test.mp4"
            stringBuffer.append("目标文件：$srcPath \n")

            var file1 = File(pcmPath)
            var file2 = File(mp4Path)
            if (file1.exists()) file1.delete()
            if (file2.exists()) file2.delete()
            file1.createNewFile()
            file2.createNewFile()

            mediaExtractor.setDataSource(srcPath)

            stringBuffer.append("TrackCount轨道个数：" + mediaExtractor.trackCount + "\n\n")

            repeat(mediaExtractor.trackCount) { i ->
                var format = mediaExtractor.getTrackFormat(i)
                var mime = format.getString(MediaFormat.KEY_MIME)
                Log.i("TAGG", "mime" + mime)
                //获取音频轨道
                if (mime.startsWith("audio")) {
                    mediaExtractor.selectTrack(i)//选择此音频轨道
                    stringBuffer.append("========音频轨道数据：开始======== \n")
                    stringBuffer.append("音频文件路径：$pcmPath \n")
                    stringBuffer.append("audio==KEY_MIME:" + format.getString(MediaFormat.KEY_MIME)!! + "\n")
                    stringBuffer.append("audio==KEY_CHANNEL_COUNT:" + format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) + "\n")
                    stringBuffer.append("audio==KEY_SAMPLE_RATE:" + format.getInteger(MediaFormat.KEY_SAMPLE_RATE) + "\n")
                    stringBuffer.append("audio==KEY_DURATION:" + format.getLong(MediaFormat.KEY_DURATION) + "\n")
                    stringBuffer.append("audio==getSampleFlags:" + mediaExtractor.sampleFlags + "\n")
                    stringBuffer.append("audio==getSampleTime:" + mediaExtractor.sampleTime + "\n")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        stringBuffer.append("audio==getSampleSize:" + mediaExtractor.sampleSize + "\n")
                    }
                    stringBuffer.append("audio==getSampleTrackIndex:" + mediaExtractor.sampleTrackIndex + "\n")

                    var inputBuffer = ByteBuffer.allocate(100 * 1024)
                    var fe = FileOutputStream(file1, true)
                    while (true) {
                        var readSampleCount = mediaExtractor.readSampleData(inputBuffer, 0)
                        if (readSampleCount < 0) break
                        var buffer = ByteArray(readSampleCount)
                        inputBuffer.get(buffer)
                        fe.write(buffer)
                        inputBuffer.clear()
                        mediaExtractor.advance()
                    }
                    fe.flush()
                    fe.close()
                    stringBuffer.append("========音频轨道数据：结束======== \n\n")
                }

                //获取视频轨道
                if (mime.startsWith("video")) {
                    mediaExtractor.selectTrack(i)//选择此视频轨道
                    stringBuffer.append("========视频轨道数据：开始======== \n")
                    stringBuffer.append("视频文件路径：$mp4Path \n")
                    stringBuffer.append("video==KEY_MIME:" + format.getString(MediaFormat.KEY_MIME) + "\n")
                    stringBuffer.append("video==KEY_WIDTH:" + format.getInteger(MediaFormat.KEY_WIDTH) + "\n")

                    stringBuffer.append("video==getSampleFlags:" + mediaExtractor.sampleFlags + "\n")
                    stringBuffer.append("video==getSampleTime:" + mediaExtractor.sampleTime + "\n")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        stringBuffer.append("video==getSampleSize:" + mediaExtractor.sampleSize + "\n")
                    }

                    var inputBuffer = ByteBuffer.allocate(100 * 1024)
                    var fe = FileOutputStream(file2, true)
                    while (true) {
                        var readSampleCount = mediaExtractor.readSampleData(inputBuffer, 0)
                        if (readSampleCount < 0) break
                        var buffer = ByteArray(readSampleCount)
                        inputBuffer.get(buffer)
                        fe.write(buffer)
                        inputBuffer.clear()
                        mediaExtractor.advance()
                    }
                    fe.flush()
                    fe.close()
                    stringBuffer.append("========视频轨道数据：结束======== \n\n")

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaExtractor.release();
            tv_extractor.text = stringBuffer.toString()
        }

    }
}
