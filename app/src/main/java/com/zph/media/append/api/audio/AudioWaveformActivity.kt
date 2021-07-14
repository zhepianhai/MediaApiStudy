package com.zph.media.append.api.audio

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.iflytek.cloud.*
import com.zph.media.R
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

    //语音识别的相关
    // 语音听写对象
    private var mIat: SpeechRecognizer? = null
    init {
//        System.loadLibrary("native-lib")
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
        setParam()
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
            //WAV转为波形显示
//            showWaveform()
            standard()
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

        val data = ByteArray(minBufferSize)
        var dir =
            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_PCM_FILE_PATH
        val filename = "${System.currentTimeMillis()}.pcm"
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
//                        waveformView.addData(getShort(data))
                        audioWaveView.setWaveData(data)
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
        val filename = "${System.currentTimeMillis()}.wav"
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

        Log.i("TAGG","--->version:"+ZPHLameUtils.getLameVersion());
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
//        standard()
        standard1()


    }
    private fun standard1(){


    }
    @Throws(InterruptedException::class)
    private fun standard() {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        //1、创建SpeechRecognizer对象，第二个参数：本地识别时传InitListener

//        mIat?.startListening(mRecognizerListener)

//        mIat?.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
//        mIat?.setParameter(SpeechConstant.ASR_SOURCE_PATH, currentFilePathWav)
        var ret = mIat?.startListening(mRecognizerListener)
        Log.i("TAG", "ret:$ret")
        if (ret !== ErrorCode.SUCCESS) {
            Log.i("TAG", "错误码$ret")
////            showTip("识别失败,错误码：$ret,请点击网址https://www.xfyun.cn/document/error-code查询解决方案")
        } else {
            Log.i("TAG", "successs")
//            val audioData: ByteArray = File(currentFilePathWav).readBytes()
//            if (null != audioData) {
//                Log.i("TAG", "ok")
////                showTip(getString(R.string.text_begin_recognizer))
//                // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），位长16bit，单声道的wav或者pcm
//                // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
//                // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
//                mIat?.writeAudio(audioData, 0, audioData.size)
//                mIat?.stopListening()
//            } else {
//                mIat?.cancel()
//                Log.i("TAG", "error")
////                showTip("读取音频流失败")
//            }
        }
    }

    private val mRecognizerListener = object : RecognizerListener {
        override fun onVolumeChanged(p0: Int, data: ByteArray?) {
//            Log.i("TAG", "返回音频数据：" + data?.size)
            Log.i("TAG", "当前正在说话，音量大小：$p0");
           waveformView.addData(p0.toShort())
        }

        override fun onResult(results: RecognizerResult?, isLast: Boolean) {
            Log.i("TAG","结果"+results?.resultString);
            results?.let { printResult(it) }
        }

        override fun onBeginOfSpeech() {
            Log.i("TAG", "onBeginOfSpeech")
        }
        //扩展用接口
        override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
            Log.i("TAG", "onEvent")
        }

        override fun onEndOfSpeech() {
            Log.i("TAG", "onEndOfSpeech")
            waveformView.clear()
        }

        override fun onError(p0: SpeechError?) {
            Log.i("TAG", "onError${p0?.errorCode}")
        }
    }

    // 用HashMap存储听写结果
    private val mIatResults: HashMap<String, String> = LinkedHashMap()
    private fun printResult(recognizerResult: RecognizerResult) {
        val text = parseIatResult(recognizerResult.resultString)
        var sn: String? = null
        //读取Json结果中的sn字段
        try {
            val resultJson = JSONObject(recognizerResult.resultString)
            sn = resultJson.optString("sn")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mIatResults[sn!!] = text!!
        val sb = StringBuilder()
        for (key in mIatResults.keys) {
            sb.append(mIatResults[key])
        }
//        tv_sppech.setText(sb.toString())
//        tv_sppech.setSelection(tv_sppech.length())
    }
    /**
     * 初始化监听器。
     */
    private val mInitListener = InitListener { code ->
        Log.i("TAG", "SpeechRecognizer init() code = $code")
        if (code != ErrorCode.SUCCESS) {
            Log.i("TAG", "初始化失败,错误码：$code,请点击网址https://www.xfyun.cn/document/error-code查询解决方案")
        }
    }

    /**
     * 参数设置
     * @return
     */
    // 引擎类型
    private var mEngineType: String? = SpeechConstant.TYPE_CLOUD

    fun setParam() {
        //参数设置
        /**
         * 应用领域 服务器为不同的应用领域，定制了不同的听写匹配引擎，使用对应的领域能获取更 高的匹配率
         * 应用领域用于听写和语音语义服务。当前支持的应用领域有：
         * 短信和日常用语：iat (默认)
         * 视频：video
         * 地图：poi
         * 音乐：music
         */
        mIat?.setParameter(SpeechConstant.DOMAIN,"iat");
        /**
         * 在听写和语音语义理解时，可通过设置此参数，选择要使用的语言区域
         * 当前支持：
         * 简体中文：zh_cn（默认）
         * 美式英文：en_us
         */
        mIat?.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        /**
         * 每一种语言区域，一般还有不同的方言，通过此参数，在听写和语音语义理解时， 设置不同的方言参数。
         * 当前仅在LANGUAGE为简体中文时，支持方言选择，其他语言区域时， 请把此参数值设为null。
         * 普通话：mandarin(默认)
         * 粤 语：cantonese
         * 四川话：lmz
         * 河南话：henanese
         */
        mIat?.setParameter(SpeechConstant.ACCENT,"mandarin");
        // 设置听写引擎
        mIat?.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        //设置语音前端点：静音超时时间，即用户多长时间不说话则当做超时处理
        //默认值：短信转写5000，其他4000
        mIat?.setParameter(SpeechConstant.VAD_BOS,"4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat?.setParameter(SpeechConstant.VAD_EOS,"1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat?.setParameter(SpeechConstant.ASR_PTT,"1");
        // 设置音频保存路径，保存音频格式支持pcm、wav
        mIat?.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        //mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
        //文本，编码
        mIat?.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");


//        var dir =
//            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_IFLY_WAV_FILE_PATH
//        val filename = "${System.currentTimeMillis()}.wav"
//        mIat?.setParameter(
//            SpeechConstant.ASR_AUDIO_PATH,
//            dir + filename
//        )
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
