package com.zph.media.append.api.audio.view

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * pcm 音频可视化
 * FF-频谱图
 *
 * */
class AudioWaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 频谱数量
     */
    private val LUMP_COUNT = 128
    private val LUMP_WIDTH = 6
    private val LUMP_MIN_HEIGHT = LUMP_WIDTH
    private val LUMP_MAX_HEIGHT = 200
    private val LUMP_SPACE = 2
    private val LUMP_COLOR = Color.parseColor("#FFBBFF")
    private val scale = 2f

    private lateinit var waveData1: ByteArray
    private  var pointList: MutableList<Point> =ArrayList()
    private lateinit var lumpPaint: Paint

   init {
       initA()
   }
    private fun initA() {
        lumpPaint = Paint()
        lumpPaint.isAntiAlias = true
        lumpPaint.strokeWidth = 6f
        lumpPaint.style = Paint.Style.STROKE
        lumpPaint.color = LUMP_COLOR
    }

    fun setwaveData1(data: ByteArray) {
        this.waveData1=data
        if(pointList==null){
            pointList=ArrayList()
        }else{
            pointList.clear()
        }
        pointList.add(Point(0, 0))
        var rate=1
        for (index in 1..data.size step rate){
            pointList.add(Point(8*(index+1), data[index].toInt()))
        }
        postInvalidate()
    }

    private var wavePath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pointList.let {
            wavePath.reset()
           for(index in 0 until pointList.size-1){
               var point=pointList[index]
               var nextPoint=pointList[index+1]
               var midX=(point.x+nextPoint.x)/2
               if(index==0){
                   wavePath.moveTo(point.x.toFloat(), point.y.toFloat())
               }
               wavePath.cubicTo(midX.toFloat(),
                   point.y.toFloat(), midX.toFloat(), nextPoint.y.toFloat(),
                   nextPoint.x.toFloat(), nextPoint.y.toFloat()
               )
           }
           canvas.drawPath(wavePath,lumpPaint)
        }

    }

}