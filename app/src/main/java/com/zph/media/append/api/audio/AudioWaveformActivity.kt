package com.zph.media.append.api.audio

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.iflytek.cloud.*
import com.zph.media.R
import com.zph.media.append.api.audio.view.AudioTrackManager
import com.zph.media.base.BaseActivity
import com.zph.media.config.AudioConfig
import com.zph.media.config.Constants
import com.zph.media.util.JsonParser.parseIatResult
import com.zph.media.util.PcmToWavUtil
import com.zph.media.util.ToastUtil
import com.zph.media.util.ZPHLameUtils
import kotlinx.android.synthetic.main.activity_audio_waveform.*
import kotlinx.android.synthetic.main.layout_navi.*
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


class AudioWaveformActivity : BaseActivity() {
    private val MY_PERMISSIONS_REQUEST = 0x16

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private var isRecording: Boolean = false
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var audioData: ByteArray = byteArrayOf()
    private var fileInputStream: FileInputStream? = null
    private var currentFilePathPcm = ""
    private var currentFilePathWav = ""

    private var mAudioTrackManage: AudioTrackManager?=null


    //语音识别的相关
    // 语音听写对象
    private var mIat: SpeechRecognizer? = null
    init {
        try {
            System.loadLibrary("lame-lib")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, AudioWaveformActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_waveform
    }

    override fun initTopBar() {
        tv_title.text = "音频和波形"
        lay_back.setOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        mIat = SpeechRecognizer.createRecognizer(this, null)
    }
    open fun setConvertProgress(progrss:Int){

    }
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var strings: Array<String> = permissions
            requestPermissions(strings, MY_PERMISSIONS_REQUEST);
            return
        }
        initAduio()
    }

    private fun initAduio() {
        btn_audio_record.setOnClickListener {
            //音频录制相关
            if (btn_audio_record.text.toString().trim() == "开始录制") {
                btn_audio_record.text = "停止录制"
                startRecord()
            } else {
                stopRecord()
                btn_audio_record.text = "开始录制"
            }
        }
        btn_audio_convert.setOnClickListener {
            //PCM转WAV
            PcmToWav()
        }
        btn_audio_Waveform.setOnClickListener {
            //Lame转换使用
            showWaveform()
        }
        btn_audio_track.setOnClickListener {
            //AduioTrack播放PCM数据格式音频
            if (btn_audio_track.text.toString().trim() == "AudioTrack播放") {
                btn_audio_track.text = "AudioTrack停止"
                audioTrackPlay()
            } else {
                audioTrackstopPlay()
                btn_audio_track.text = "AudioTrack播放"
            }
        }
    }

    /**
     * 开始录制音频
     * */

    private fun startRecord() {
        //采样率 - 声道数 --返回的音频数据的格式
        val minBufferSize: Int = AudioRecord.getMinBufferSize(
            AudioConfig.SAMPLE_RATE_INHZ,
            AudioConfig.CHANNEL_CONFIG,
            AudioConfig.AUDIO_FORMAT
        )
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, AudioConfig.SAMPLE_RATE_INHZ, AudioConfig.CHANNEL_CONFIG,
            AudioConfig.AUDIO_FORMAT, minBufferSize
        )
        if(AudioRecord.STATE_INITIALIZED!=audioRecord!!.state){
            ToastUtil.showToast(this,"AudioRecord无法初始化，请检查录制权限或者是否其他app没有释放录音\n" +
                    "器")
            return
        }
        val data = ByteArray(minBufferSize)
        var sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var timeString = sdf.format(Date())
        var dir =
            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_PCM_FILE_PATH
        val filename = "${timeString}.pcm"
        val file = File(dir, filename)
        if (file.exists()) {
            file.delete()
        }
        audioRecord?.startRecording()

        isRecording = true
        //pcm数据无法直接播放，保存为wav格式
        Thread(Runnable {
            var os: FileOutputStream? = null
            try {
                os = FileOutputStream(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            while (isRecording) {
                var read: Int = audioRecord?.read(data, 0, minBufferSize)!!

                //如果读取音频数据没有出现错误，就将数据写到文件
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os!!.write(data)
                        Log.i("TAGG",data.toString())
//                        waveformView.addData(getShort(data))
                        audioWaveView.setwaveData1(data)
                        runOnUiThread {
                            audioWaveView.invalidate()
                        }
                    } catch (e: Exception) {
                    }
                }
            }
            currentFilePathPcm = file.absolutePath
            try {
                Log.i("TAG", "run: close file output stream !");
                os!!.close()
            } catch (e: Exception) {
            }
        }).start()
    }

    fun getShort(b: ByteArray): Short {
        return (((b[1].toInt() shl 8) or b[0].toInt() and 0xff).toShort())
    }

    private fun stopRecord() {
        isRecording = false
        //释放资源
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
        audioWaveView.invalidate()
    }

    /**
     * PCM转换为WAV
     * */
    private fun PcmToWav() {
        if (TextUtils.isEmpty(currentFilePathPcm)) {
            ToastUtil.showToast(this@AudioWaveformActivity, "请先录制一段音频！")
            return
        }
        var pcmFile = File(currentFilePathPcm)
        if (!pcmFile.exists()) {
            ToastUtil.showToast(this@AudioWaveformActivity, "请先录制一段音频！")
            return
        }
        var dir =
            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_WAV_FILE_PATH
        var sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var wavString = sdf.format(Date())
        val filename = "${wavString}.wav"
        var wavFile = File(dir, filename)
        if (wavFile.exists()) {
            wavFile.delete()
        }
        var pcmToWavUtil = PcmToWavUtil(
            AudioConfig.SAMPLE_RATE_INHZ,
            AudioConfig.CHANNEL_CONFIG,
            AudioConfig.AUDIO_FORMAT
        )
        pcmToWavUtil.pcmToWav(pcmFile.absolutePath, wavFile.absolutePath)
        currentFilePathWav = wavFile.absolutePath
        ToastUtil.showToast(this@AudioWaveformActivity, "转换完成！")
    }
    /**
     * AudioTrack 播放pcm源格式数据
     * 首先它可以直接播放pcm音频数据，但是一般是不能播放其它的格式如MP3，AAC，WAV等,
     * */
    private fun audioTrackPlay(){
        var pcmFile = File(currentFilePathPcm)
        if (!pcmFile.exists()) {
            ToastUtil.showToast(this@AudioWaveformActivity, "PCM数据格式音频不存在！")
            return
        }
        if(mAudioTrackManage==null){
            mAudioTrackManage= AudioTrackManager()
        }
        mAudioTrackManage!!.startPlay(pcmFile.absolutePath)
    }
    private fun audioTrackstopPlay(){
        mAudioTrackManage!!.stopPlay()
    }


    /**
     * 转换为文本
     * */
    private fun showWaveform() {
        if (TextUtils.isEmpty(currentFilePathWav)) {
            ToastUtil.showToast(this@AudioWaveformActivity, "Wav音频不存在！")
            return
        }
        var wavFile = File(currentFilePathWav)
        if (!wavFile.exists()) {
            ToastUtil.showToast(this@AudioWaveformActivity, "Wav音频不存在！")
            return
        }
        standard()
    }

    @SuppressLint("SetTextI18n")
    @Throws(InterruptedException::class)
    private fun standard() {
        tvLameVersion.text="当前的Lame版本号是："+ZPHLameUtils.getLameVersion()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initAduio()
            } else {
                ToastUtil.showToast(this@AudioWaveformActivity, "拒绝权限无法使用！")
            }
        }

    }
}
