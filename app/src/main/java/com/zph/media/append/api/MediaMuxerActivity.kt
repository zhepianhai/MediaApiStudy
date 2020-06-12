package com.zph.media.append.api

import android.app.Activity
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.os.Environment
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.config.Constants
import com.zph.media.util.FileUtil
import com.zph.media.util.ToastUtil
import kotlinx.android.synthetic.main.activity_media_muxer.*
import kotlinx.android.synthetic.main.layout_navi.*
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.abs


/**
 *
 * 在Android中，可以使用MediaMuxer来封装编码后的视频流和音频流到mp4容器中：
 *
 *
 * @说明 h264   aac   muxer 成mp4
 * 直接muxer h264和aac 无法muxer，
 * audioExtractor.setDataSource(sdcard_path + "/input.aac");无法读取
 * 1.需要将h264先混合成mpeg4包装的mp4（无音频）
 * 2.需要将aac（无adts）先混合成mpeg4容器包装的mp4（无视频）
 * 3.muxer混合包装好的音频和视频（分别从包装好的中重新分离出来aac和H264），生成新的视频文件
 * */


class MediaMuxerActivity : BaseActivity() {

    lateinit var mMediaMuxer: MediaMuxer
    private var mediaExtractor1 = MediaExtractor()
    private var mediaExtractor2 = MediaExtractor()


    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, MediaMuxerActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_media_muxer
    }

    override fun initTopBar() {
        tv_title.text = "MediaExtractor"
        lay_back.setOnClickListener {
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkFile()
        initListener()
    }

    private fun initListener() {
        btn_muxer.setOnClickListener {
            Mp4_toMp4_MediaMuxer()
        }
    }

    private fun checkFile() {
        var mp4Path =
            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/test.mp4"
        var file1 = File(mp4Path)
        if (file1.exists() ) {
            showDialogSuccess()
        } else {
            showDialogError()
        }
    }

    private fun showDialogSuccess() {
        QMUIDialog.MessageDialogBuilder(this)
            .setTitle("提示")
            .setMessage("【test.mp4】文件存在可以进行封装编码操作!")
            .addAction("确定", QMUIDialogAction.ActionListener { dialog, _ ->
                dialog.dismiss()
            }).setCanceledOnTouchOutside(false)
            .show();
    }

    private fun showDialogError() {
        QMUIDialog.MessageDialogBuilder(this)
            .setTitle("提示")
            .setMessage("【test.mp4】文件不存在无法进行操作，是否读取test.mp4数据？")
            .addAction("退出", QMUIDialogAction.ActionListener { dialog, _ ->
                dialog.dismiss()
                finish()

            })
            .addAction(
                0,
                "读取",
                QMUIDialogAction.ACTION_PROP_NEGATIVE,
                QMUIDialogAction.ActionListener { dialog, _ ->

                    FileUtil.copyFilesFromRaw(this,R.raw.test,"test.mp4",
                        Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_+Constants.ZPH_SOURCE_PATH)
                    ToastUtil.showToast(this,"读取成功")
                    dialog.dismiss()
                })
            .setCanceledOnTouchOutside(false)
            .show();

    }
    /**
     *
     * 直接用源文件的mp4.拆分h264和acc合成Mp4
     * */
    private fun Mp4_toMp4_MediaMuxer() {
        var stringBuffer = StringBuffer()
        try {
            tv_muxer.text=""


            val desPath =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_CACHE_PATH + "/test_muxer.mp4"
            var mp4Path =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/test.mp4"
            var pcmPath =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_SOURCE_PATH + "/test.mp4"
            stringBuffer.append("合成目录：$desPath \n")
            val filedes = File(desPath)
            val videoPath = File(mp4Path)
            val audioPath = File(pcmPath)
            if (filedes.exists()) {
                filedes.delete();
            }
            filedes.createNewFile();
            mMediaMuxer = MediaMuxer(desPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            var mVideoTrackIndex = 0
            var mAudioTrackIndex = 0
            var frameRate1: Long = 0
            var frameRate2: Long = 0

            var format1: MediaFormat
            var format2: MediaFormat

            //视频：Video的
            try {
                mediaExtractor1.setDataSource(mp4Path)
                repeat(mediaExtractor1.trackCount) { i ->
                    format1 = mediaExtractor1.getTrackFormat(i)
                    var mine = format1.getString(MediaFormat.KEY_MIME)
                    if (mine.startsWith("video")) {
                        //获取器视频轨道
                        mediaExtractor1.selectTrack(i)
                        frameRate1 = format1.getInteger(MediaFormat.KEY_FRAME_RATE).toLong()
                        mVideoTrackIndex = mMediaMuxer.addTrack(format1)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            //音频的
            try {
                mediaExtractor2.setDataSource(pcmPath)
                repeat(mediaExtractor2.trackCount) { i ->
                    format2 = mediaExtractor2.getTrackFormat(i)
                    val mime = format2.getString(MediaFormat.KEY_MIME)
                    if (mime.startsWith("audio")) {
                        var buffer = ByteBuffer.allocate(100 * 1024)
                        mediaExtractor2.selectTrack(i)
                        mediaExtractor2.readSampleData(buffer, 0)
                        val first_sampletime = mediaExtractor2.sampleTime
                        mediaExtractor2.advance()
                        val second_sampletime = mediaExtractor2.sampleTime
                        frameRate2 = abs(second_sampletime - frameRate1)//时间戳
                        mediaExtractor2.unselectTrack(i)

                        mediaExtractor2.selectTrack(i)
                        mAudioTrackIndex = mMediaMuxer.addTrack(format2)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            //开始合成
            mMediaMuxer.start()
            //视频部分
            var info1 = MediaCodec.BufferInfo()
            info1.presentationTimeUs = 0
            var buffer = ByteBuffer.allocate(100 * 1024)
            var sampleSize1 = 0
            while ((mediaExtractor1.readSampleData(buffer, 0) > 0)) {
                sampleSize1 = mediaExtractor1.readSampleData(buffer, 0)
                info1.offset = 0;
                info1.size = sampleSize1;
                info1.flags = mediaExtractor1.getSampleFlags();
                info1.presentationTimeUs += 1000 * 1000 / frameRate1;
                mMediaMuxer.writeSampleData(mVideoTrackIndex, buffer, info1);
                mediaExtractor1.advance();
            }
            //音频部分
            var info2 = MediaCodec.BufferInfo()
            info2.presentationTimeUs = 0
            var sampleSize2 = 0
            while (mediaExtractor2.readSampleData(buffer, 0) > 0) {
                sampleSize2 = mediaExtractor2.readSampleData(buffer, 0)
                info2.offset = 0;
                info2.size = sampleSize2;
                info2.flags = mediaExtractor2.getSampleFlags();
                info2.presentationTimeUs += frameRate2;
                mMediaMuxer.writeSampleData(mAudioTrackIndex, buffer, info2);
                mediaExtractor2.advance();
            }

            mediaExtractor1.release();
            mediaExtractor2.release();
            mMediaMuxer.stop();
            mMediaMuxer.release();
            stringBuffer.append("完成")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tv_muxer.text=stringBuffer.toString()
        }
    }


}
