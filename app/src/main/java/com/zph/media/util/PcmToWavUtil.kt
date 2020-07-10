package com.zph.media.util

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 将pcm音频文件转换为wav音频文件
 *
 *
 * @param mSampleRate sample rate、采样率
 * @param mChannel channel、声道
 * @param encoding Audio data format、音频格式

 */
 class PcmToWavUtil(mSampleRate: Int, mChannel: Int, encoding: Int) {

    /**
     * 缓存的音频大小
     */
    private var mBufferSize = 0

    /**
     * 采样率
     */
    private var mSampleRate = 0

    /**
     * 声道数
     */
    private var mChannel = 0

    init {
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, encoding)
        this.mSampleRate = mSampleRate
        this.mChannel = mChannel
    }


    /**
     * pcm文件转wav文件
     *
     * @param inFileName 源文件路径
     * @param outFileName 目标文件路径
     */

     fun pcmToWav(inFileName:String,outFileName:String){
        var input:FileInputStream
        var output:FileOutputStream
        var totalAudioLen:Long
        var totalDataLen:Long
        var longSampleRate=mSampleRate
        var channels=if(mChannel==AudioFormat.CHANNEL_IN_MONO) 1 else 2
        var byteRate=16*mSampleRate*channels/8
        var data= ByteArray(mBufferSize)
        try {
            input= FileInputStream(inFileName)
            output= FileOutputStream(outFileName)
            totalAudioLen=input.channel.size()
            totalDataLen=totalAudioLen+36

            writeWaveFileHeader(output,totalAudioLen,totalDataLen,
                longSampleRate.toLong(),channels, byteRate)

            while (input.read(data)!=-1){
                output.write(data)
            }
            input.close()
            output.close()

        }catch (e:Exception){}
    }

    /**
     * 加入wav文件头
     */
    fun writeWaveFileHeader(out:FileOutputStream,totalAudioLen:Long,totalDataLen:Long,longSampleRate:Long,channels:Int,byteRate:Int){
        try {
            var header:ByteArray=ByteArray(44)
            // RIFF/WAVE header
            header[0] = 'R'.toByte()
            header[1] = 'I'.toByte()
            header[2] = 'F'.toByte()
            header[3] = 'F'.toByte()
            header[4] =  ((totalDataLen and 0xff))as Byte
            header[5] =  (((totalDataLen shr   8) and 0xff))as Byte
            header[6] =  (((totalDataLen shr  16) and 0xff))as Byte
            header[7] =  (((totalDataLen shr  24) and 0xff))as Byte
            //WAVE
            header[8] = 'W'.toByte()
            header[9] = 'A'.toByte()
            header[10] = 'V'.toByte()
            header[11] = 'E'.toByte()
            // 'fmt ' chunk
            header[12] = 'f'.toByte()
            header[13] = 'm'.toByte()
            header[14] = 't'.toByte()
            header[15] = ' '.toByte()
            // 4 bytes: size of 'fmt ' chunk
            header[16] = 16;
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            // format = 1
            header[20] = 1;
            header[21] = 0;
            header[22] = channels.toByte()
            header[23] = 0;
            header[24] =  ((longSampleRate and  0xff))as Byte
            header[25] = ((longSampleRate shr 8) and  0xff)as Byte
            header[26] = ((longSampleRate shr 16) and 0xff)as Byte
            header[27] = ((longSampleRate shr 24) and 0xff)as Byte
            header[28] = (byteRate and 0xff)as Byte
            header[29] =  ((byteRate shr 8) and 0xff)as Byte
            header[30] =  ((byteRate shr 16) and 0xff)as Byte
            header[31] =  ((byteRate shr 24) and 0xff)as Byte
            // block align
            header[32] = (2 * 16 / 8);
            header[33] = 0;
            // bits per sample
            header[34] = 16;
            header[35] = 0;
            //data
            header[36] = 'd'.toByte()
            header[37] = 'a'.toByte()
            header[38] = 't'.toByte()
            header[39] = 'a'.toByte()
            header[40] =  (totalAudioLen and 0xff) as Byte
            header[41] = ((totalAudioLen shr  8) and 0xff)as Byte
            header[42] =  ((totalAudioLen shr 16) and 0xff) as Byte
            header[43] =  ((totalAudioLen shr 24) and 0xff) as Byte
            out.write(header, 0, 44);


        }catch (e:Exception){}
    }


}