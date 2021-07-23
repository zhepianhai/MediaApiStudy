package com.zph.media.append.api.audio.view

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream


/**
 * AudioTrack  的封装
 * 用于播放PCM
 * */
class AudioTrackManager {
    private var mAudioTrack: AudioTrack?=null
    private var mDis: DataInputStream?=null  //播放文件的数据流
    private var mRecordThread: Thread? = null
    private var isStart = false

    @Volatile
    private var mInstance: AudioTrackManager? = null

    //音频流类型
    private val mStreamType = AudioManager.STREAM_MUSIC

    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。
    // 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private val mSampleRateInHz = 44100

    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private val mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO //单声道

    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。
    // 通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private val mAudioFormat = AudioFormat.ENCODING_PCM_16BIT

    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private var mMinBufferSize = 0

    //STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。这个和我们在socket中发送数据一样，
    // 应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
    private var mMode = AudioTrack.MODE_STREAM

    private fun AudioTrackManager(): AudioTrackManager {
        initData()
        return this
    }

    /**
     * 单例
     * */
    fun getInstance(): AudioTrackManager? {
        if (mInstance == null) {
            synchronized(AudioTrackManager::class.java) {
                if (mInstance == null) {
                    mInstance = AudioTrackManager()
                }
            }
        }
        return mInstance
    }

    fun initData() {
        //根据采样率，采样精度，单双声道来得到frame的大小。
        mMinBufferSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat)

        //注意，按照数字音频的知识，这个算出来的是一秒钟buffer的大小。
        //创建AudioTrack
        mAudioTrack = AudioTrack(
            mStreamType, mSampleRateInHz, mChannelConfig,
            mAudioFormat, mMinBufferSize, mMode
        )
    }

    /**
     * 启动线程
     * */
    private fun startThread() {
        destroyThread()
        isStart = true
        if (mRecordThread == null) {
            mRecordThread = Thread(recordRunnable)
            mRecordThread!!.start()
        }
    }

    /**
     * 播放线程
     * */
    private var recordRunnable = Runnable {
        try {
            //设置优先级
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            var tempBuffer = ByteArray(mMinBufferSize)
            var readCount = 0
            while (mDis!!.available() > 0) {
                readCount = mDis!!.read(tempBuffer)
                if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                    continue
                }
                if (readCount != 0 && readCount != -1) {
                    //判断AudioTrack未初始化，停止播放的时候释放了，状态就为STATE_UNINITIALIZED
                    if (mAudioTrack!!.state == AudioTrack.STATE_UNINITIALIZED) {
                        initData()
                    }
                    //采用一边播放一边写入语音的模式进行，
                    mAudioTrack!!.play()
                    mAudioTrack!!.write(tempBuffer, 0, readCount)
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 设置PCM文件路径
     *
     * */
    @Throws(java.lang.Exception::class)
    private fun setPath(path: String) {
        val file = File(path)
        mDis = DataInputStream(FileInputStream(file))
    }

    /**
     * 启动播放
     * */
    fun startPlay(path: String) {
        setPath(path)
        startThread()
    }
    /**
     * 停止播放
     * */
    fun stopPlay(){
        try {
            destroyThread()
            if(mAudioTrack!!.state==AudioTrack.STATE_INITIALIZED){
                mAudioTrack!!.stop()//停止
            }
            mAudioTrack!!.release()//释放资源
            mDis!!.close()//关闭输入流

        }catch (e:java.lang.Exception){

        }
    }


    /**
     * 销毁线程
     * */
    private fun destroyThread() {
        try {
            isStart = false
            if (mRecordThread!!.state == Thread.State.RUNNABLE) {
                try {
                    Thread.sleep(500)
                    mRecordThread!!.interrupt()
                } catch (e: Exception) {
                    mRecordThread = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mRecordThread = null
        }
    }


}