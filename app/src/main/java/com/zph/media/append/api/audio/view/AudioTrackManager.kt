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
    private var mAudioTrack: AudioTrack? = null
    private var mDis: DataInputStream? = null  //播放文件的数据流
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
    fun stopPlay() {
        try {
            destroyThread()
            if (mAudioTrack!!.state == AudioTrack.STATE_INITIALIZED) {
                mAudioTrack!!.stop()//停止
            }
            mAudioTrack!!.release()//释放资源
            mDis!!.close()//关闭输入流

        } catch (e: java.lang.Exception) {

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
/**
 * 在实践中遇到了不少问题，不过最终都得以解决，记录如下
1: stream模式快速点击 声音重叠，如何停止：在触发播放前先停止和释放auidoTrack，然后在进行init，
在audioTrack写入数据的线程中write操作要做好audiotTrack的状态判断。具体实现见上面小节的 代码
2：如何监听播放进度：AudioTrack有没有想MediaPlayer的丰富的监听回调，比如说，播放进度，播放
完成回调，异常回调等。遗憾的是还真没有，针对STATIC模式的播放结束监听倒是可以借助
setNotificationMarkerPosition 和 setPlaybackPositionUpdateListener来判断来判断。具体见上面小
节中STATIC模式的实现
3: staic模式下有时候无法播放；音频在快速连续点击中加了isplaying的片段，如果正在playing中有触发
了play，会先stop然后调用audioTrack.reloadStaticData()加载数据流，再进行播放，但是发现快速 连
续点击是间隔一次才会播放生效，原因还是audioTrack资源没有被正确使用，改为了先release在进行init
的方式。
4: IllegalStateException: Unable to retrieve AudioTrack pointer for write()：这个异常是stream模式
时在主线程出发了stop或者release，而在audioTrack子线程write时抛出的异常，原因就是播放状态不
对，如果已经处于Stropped状态，再进行write操作就会报这个错误，所以write时加个playstate状态的
检验。

 **/


