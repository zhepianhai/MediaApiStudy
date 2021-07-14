package com.zph.media.append.api

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.config.AudioConfig.Companion.AUDIO_FORMAT
import com.zph.media.config.AudioConfig.Companion.CHANNEL_CONFIG
import com.zph.media.config.AudioConfig.Companion.SAMPLE_RATE_INHZ
import com.zph.media.util.PcmToWavUtil
import kotlinx.android.synthetic.main.activity_android_audio_api.*
import kotlinx.android.synthetic.main.layout_navi.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

/**
 *
 * AudioTrack是什么？ 
AudioRecord是可以播放原始音频数据pcm的api，
pcm一般的播放器都是无法播放的，AudioRecord可以播放pcm，不过需要制定播放时候的采样率、声道数位宽，
现在在android下面做了一个demo，主要是播放pcm录音文件。pcm录音时候需要制定几个重要参数，播放的时候还需要设置录制缓冲区大小，缓存区越大，内存溢出风险越小。

pcm参数：
1、采样率 
2、声道数 
3、位宽
 *
 * */
class AndroidAudioApiActivity : BaseActivity() {
    private val MY_PERMISSIONS_REQUEST=0x16

    /**
     * 需要申请的运行时权限
     */
    private var permissions :Array<String> = arrayOf(RECORD_AUDIO,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mPermissionList:MutableList<String> = arrayListOf()

    private var isRecording :Boolean=false
    private lateinit var audioRecord: AudioRecord
    private lateinit var audioTrack: AudioTrack
    private var audioData: ByteArray= byteArrayOf()
    private lateinit var fileInputStream:FileInputStream


    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, AndroidAudioApiActivity::class.java)
            activity.startActivity(intent)
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_android_audio_api
    }

    override fun initTopBar() {
        tv_title.text = "AudioTrack"
        lay_back.setOnClickListener {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
        checkPermissions()
    }



    private fun checkPermissions(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var strings: Array<String> =permissions
            requestPermissions(strings, MY_PERMISSIONS_REQUEST);
            return
        }

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
//            for((index,e) in permissions.withIndex()){
//                if(ContextCompat.checkSelfPermission(this,e)!=PackageManager.PERMISSION_GRANTED){
//                    mPermissionList.add(e)
//                }
//            }
//
//            if(mPermissionList.isNotEmpty()){
//                var permissions :Array<String> = mPermissionList.toTypedArray()
//                ActivityCompat.requestPermissions(this,permissions,MY_PERMISSIONS_REQUEST)
//            }
//        }
    }

    override fun onRequestPermissionsResult(  requestCode: Int, permissions: Array<out String>,  grantResults: IntArray    ) {
        if(requestCode==MY_PERMISSIONS_REQUEST){
            for((_,e) in grantResults.withIndex()){
                if(e==PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(this,"权限被用户禁止！",Toast.LENGTH_SHORT).show()
//                    finish()
                }
            }
        }

    }

    private fun initListener() {
        btn_pcm_control.setOnClickListener {
            if(btn_pcm_control.text.toString().trim() == "start_record"){
                btn_pcm_control.text = "stop_record"
                startRecord()
            }else{
                btn_pcm_control.text = "start_record"
                stopRecord()
            }
        }

        btn_pcm_convert.setOnClickListener {
            onclickTest()


        }
        btn_pcm_play.setOnClickListener {
            var string:String=btn_pcm_play.text.toString().trim()
            if(string == "start_play"){
                btn_pcm_play.text="stop_play"
                palyInModeStream()
            }else{
                btn_pcm_play.text="start_play"
                stopPlay()
            }
        }
    }

    private fun onclickTest() {
        var pcmToWavUtil= PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        var pcmFile=File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"zph_test.pcm")
        var wavFile=File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"zph_test.wav")
        if(!wavFile.mkdirs()){
            Log.e("TAG", "wavFile Directory not created");
        }
        if(wavFile.exists()){
            wavFile.delete()
        }
        pcmToWavUtil.pcmToWav(pcmFile.absolutePath,wavFile.absolutePath)
    }

    private fun startRecord() {
        //采样率 - 声道数 --返回的音频数据的格式
        val minBufferSize:Int=AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG,AUDIO_FORMAT)
        audioRecord= AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG,
            AUDIO_FORMAT,minBufferSize)

        val data= ByteArray(minBufferSize)
        val file=File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"zph_test.pcm")
        if(!file.mkdirs()){
            Log.e("TAG", "Directory not created");
        }
        if(file.exists()){
            file.delete()
        }
        if(audioRecord.state==AudioRecord.STATE_UNINITIALIZED){
            throw RuntimeException("the audioRecord is not uninitialized")
        }
        audioRecord.startRecording()
        isRecording=true
        //pcm数据无法直接播放，保存为wav格式
        Thread(Runnable {
            var os : FileOutputStream? =null
            try {
                os=FileOutputStream(file)
            }catch (e:Exception){e.printStackTrace()}
            while (isRecording){
                var read:Int=audioRecord.read(data,0,minBufferSize)
                //如果读取音频数据没有出现错误，就将数据写到文件
                if(AudioRecord.ERROR_INVALID_OPERATION!=read){
                    try {
                        os!!.write(data)
                    }catch (e:Exception){}
                }
            }
            try {
                Log.i("TAG", "run: close file output stream !");
                os!!.close()
            }catch (e:Exception){}
        }).start()

    }


    private fun stopRecord(){
        isRecording=false
        //释放资源
        audioRecord!!.stop()
        audioRecord!!.release()
    }

    /**
     * 播放，使用stream模式
     */

    private fun palyInModeStream(){
        /*
       * SAMPLE_RATE_INHZ 对应pcm音频的采样率
       * channelConfig 对应pcm音频的声道
       * AUDIO_FORMAT 对应pcm音频的格式
       * */
        var channelConfig=AudioFormat.CHANNEL_OUT_MONO
        val minBufferSize=AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ,channelConfig, AUDIO_FORMAT)
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT){
            audioTrack= AudioTrack(
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
                AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                    .setEncoding(AUDIO_FORMAT).setChannelIndexMask(channelConfig).build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
        }else{
            audioTrack= AudioTrack(AudioManager.STREAM_MUSIC,
            48000,
            channelConfig,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize,
            AudioTrack.MODE_STREAM)
        }
        audioTrack.play()
        var file=File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"zph_test.pcm")
        try {
            fileInputStream= FileInputStream(file)
            Thread(Runnable {
                try {
                    var tempBuffer:ByteArray= ByteArray(minBufferSize)
                    while (fileInputStream.available()>0){
                        var readCount:Int=fileInputStream.read(tempBuffer)
                        if(readCount==AudioTrack.ERROR_INVALID_OPERATION
                            ||readCount==AudioTrack.ERROR_BAD_VALUE){
                            continue
                        }
                        if(readCount!=0&&readCount!=-1){
                            audioTrack.write(tempBuffer,0,readCount)
                        }
                    }

                }catch (e:Exception){}
            }).start()

        }catch (e:Exception){}

    }

    private fun stopPlay(){
        audioTrack!!.stop()
        audioTrack!!.release()
    }
}
