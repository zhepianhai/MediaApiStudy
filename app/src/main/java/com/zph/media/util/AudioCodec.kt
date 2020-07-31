package com.zph.media.util

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Handler
import android.os.Looper
import com.zph.media.append.api.codec.runnable.AudioDecodeRunnable
import com.zph.media.append.api.codec.runnable.AudioEncodeRunnable


/**
 *@description  音频相关的操作类
 *
 *@author zph
 *@date 2020年6月16日11:46:52
 */
class AudioCodec {

    companion object {
        private const val TAG = "AudioCodec"
        private val handler: Handler = Handler(Looper.getMainLooper())

        /**
         * 将音频文件解码成原始的PCM数据
         * @param audioPath         音频文件目录
         * @param audioSavePath     pcm文件保存位置
         * @param listener
         */


        /**
         * 将音频文件解码成原始的PCM数据
         * */
         fun getPCMFromAudio(mp3SourcePath:String ,pcmSavePath:String,listener:AudioDecodeListener){

            var extractor=MediaExtractor()//此类可分离视频文件的音轨和视频轨道
            var audioTrack=-1//音频MP3文件其实只有一个音轨
            var hasAudio=false//判断音频文件是否有音频音轨
            try {
                extractor.setDataSource(mp3SourcePath)
                repeat(extractor.trackCount){i->
                    var format=extractor.getTrackFormat(i)
                    var mime=format.getString(MediaFormat.KEY_MIME)
                    if(mime.startsWith("audio/")){
                        audioTrack=i
                        hasAudio=true
                        return@repeat
                    }
                }
                if(hasAudio){
                    extractor.selectTrack(audioTrack)
                    //原始的音频解码
                    Thread(
                        AudioDecodeRunnable(
                            extractor,
                            audioTrack,
                            pcmSavePath,
                            object : DecodeOverListener {
                                override fun decodeIsOver() {
                                    handler.post(Runnable {
                                        listener?.decodeOver()
                                    })
                                }

                                override fun decodeFail() {
                                    handler.post(Runnable {
                                        if (listener != null) {
                                            listener.decodeFail()
                                        }
                                    })
                                }
                            })
                    ).start()
                }

            }catch (e:Exception){
                e.printStackTrace()
            }


        }


        /**
         * pcm文件转音频
         * @param pcmPath       pcm文件目录
         * @param audioPath     音频文件目录
         * @param listener
         */
        fun PcmToAudio(
            pcmPath: String?,
            audioPath: String?,
            listener: AudioDecodeListener?
        ) {
            Thread(AudioEncodeRunnable(pcmPath!!, audioPath!!, object : AudioDecodeListener {
                override fun decodeOver() {
                    if (listener != null) {
                        handler.post { listener.decodeOver() }
                    }
                }

                override fun decodeFail() {
                    if (listener != null) {
                        handler.post { listener.decodeFail() }
                    }
                }
            })).start()
        }














        /**
         * 写入ADTS头部数据
         * @param packet
         * @param packetLen
         */
        fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
            val profile = 2 // AAC LC
            val freqIdx = 4 // 44.1KHz
            val chanCfg = 2 // CPE
            packet[0] = 0xFF.toByte()
            packet[1] = 0xF9.toByte()
            packet[2] =
                ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
            packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
            packet[4] = (packetLen and 0x7FF shr 3).toByte()
            packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
            packet[6] = 0xFC.toByte()
        }
    }


     interface DecodeOverListener {
        fun decodeIsOver()
        fun decodeFail()
    }

    /**
     * 音频解码监听器：监听是否解码成功
     */
     interface AudioDecodeListener {
        fun decodeOver()
        fun decodeFail()
    }

}