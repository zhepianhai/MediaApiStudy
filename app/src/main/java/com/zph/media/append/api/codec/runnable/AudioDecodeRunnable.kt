package com.zph.media.append.api.codec.runnable

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import com.zph.media.util.AudioCodec
import java.io.FileOutputStream
import java.nio.ByteBuffer


/**
 *@description 音频解码过程
 *
 *@author zph
 *@date 2020年6月16日11:34:28
 * 112  47.5
 */
class AudioDecodeRunnable(
    extractor: MediaExtractor?,
    trackIndex: Int,
    savePath: String,
    listener: AudioCodec.DecodeOverListener
) : Runnable {

    val TIMEOUT_USEC = 0
    var extractor: MediaExtractor
    var audioTrack = 0
    var mListener: AudioCodec.DecodeOverListener
    private var mPcmFilePath: String? = null

    init {
        this.extractor = extractor!!
        this.audioTrack = trackIndex
        this.mListener = listener
        this.mPcmFilePath = savePath
    }

    override fun run() {
        try {
            var format = extractor.getTrackFormat(audioTrack)
            //初始化音频解码器
            var audioCodec=MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME))
            audioCodec.configure(format,null,null,0)
            audioCodec.start()//启动MediaCodec 等待传入数据

            var inputBuffers=audioCodec.inputBuffers//MeidaCodec在ByteBuffer中获取数据
            var outputBuffers=audioCodec.outputBuffers////MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据

            var decodeBufferInfo=MediaCodec.BufferInfo()
            var inputInfo=MediaCodec.BufferInfo()
            var codeOver=false
            var inputDone=false//整体输入结束标志

            var fos=FileOutputStream(mPcmFilePath)

            while (!codeOver){
//                首先向mediacodec放入数据
                //dequeueInputBuffer方法返回值大于-1时，表示输入可用
                //
                //getInputBuffer：获取输入buffer，数据放入这个里面
                //
                //queueInputBuffer：通知中间处理过程，去处理数据
                //

                if (!inputDone) {
                    repeat(inputBuffers.size) { _ ->
                        //将数据传入之后，再去输出端取出数据
                        val inputIndex = audioCodec.dequeueInputBuffer(TIMEOUT_USEC.toLong())
                        if (inputIndex > 0) {
                            //从分离器拿出输入，写入解码器
                            var inputBuffer: ByteBuffer =
                                inputBuffers[inputIndex] //拿到inputBuffer，新的API中好像可以直接拿到
                            inputBuffer.clear()
                            //MediaExtractor读取数据
                            var sampleSize = extractor.readSampleData(
                                inputBuffer,
                                0
                            ) //将MediaExtractor读取数据到inputBuffer
                            if (sampleSize < 0) {//表示所有数据已经读取完毕
                                audioCodec.queueInputBuffer(
                                    inputIndex,
                                    0,
                                    0,
                                    0L,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                );
                            } else {
                                inputInfo.offset = 0;
                                inputInfo.size = sampleSize;
                                inputInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
                                inputInfo.presentationTimeUs = extractor.getSampleTime();

                                Log.e("TAG", "往解码器写入数据，当前时间戳：" + inputInfo.presentationTimeUs);
                                //通知MediaCodec解码刚刚传入的数据
                                audioCodec.queueInputBuffer(
                                    inputIndex,
                                    inputInfo.offset,
                                    sampleSize,
                                    inputInfo.presentationTimeUs,
                                    0
                                );
                                extractor.advance();
                            }

                        }
                    }
                }
//                然后取出数据，（注意：放入一次数据，取出时数据次数不定），需要多次取，
//                直到这次放入的数据没有没有对应的输出
//                dequeueOutputBuffer：结果大于-1时，表示有输出数据
//                getOutputBuffer：输出buffer，数据在这里面，dequeueOutputBuffer这里面参数info是buffer中的数据信息
//                releaseOutputBuffer：释放buffer

                var decodeOutputDone = false
                var chunkPCM: ByteArray
                while (!decodeOutputDone){
                    var outputIndex =
                        audioCodec.dequeueOutputBuffer(decodeBufferInfo, TIMEOUT_USEC.toLong())
                    if (outputIndex === MediaCodec.INFO_TRY_AGAIN_LATER) {
                        //没有可用的解码器
                        decodeOutputDone = true
                    } else if (outputIndex === MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        outputBuffers = audioCodec.outputBuffers
                    } else if (outputIndex === MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val newFormat = audioCodec.outputFormat
                    } else if (outputIndex < 0) {
                    } else {
                        var outputBuffer: ByteBuffer? = if (Build.VERSION.SDK_INT >= 21) {
                            audioCodec.getOutputBuffer(outputIndex)
                        } else {
                            outputBuffers[outputIndex]
                        }
                        chunkPCM = ByteArray(decodeBufferInfo.size)
                        outputBuffer!![chunkPCM]
                        outputBuffer!!.clear()
                        fos.write(chunkPCM) //数据写入文件中
                        fos.flush()
                        Log.e("TAG", "释放输出流缓冲区：$outputIndex")
                        audioCodec.releaseOutputBuffer(outputIndex, false)
                        if (decodeBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM !== 0) { //编解码结束
                            extractor.release()
                            audioCodec.stop()
                            audioCodec.release()
                            codeOver = true
                            decodeOutputDone = true
                        }
                    }
                }

            }
            fos.close();
            mListener.decodeIsOver();
            if (mListener != null){
                mListener.decodeIsOver();
            }

        }catch (e:Exception){
            e.printStackTrace()
            if (mListener != null){
                mListener.decodeFail();
            }
        }
    }
}