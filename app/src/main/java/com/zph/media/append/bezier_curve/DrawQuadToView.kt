package com.zph.media.append.bezier_curve

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 贝塞尔曲线自定义View
 * */
@Suppress("UNREACHABLE_CODE")
class DrawQuadToView @kotlin.jvm.JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var eventX=0f
    private var eventY=0f
    private var centerX=0f
    private var centerY=0f
    private var startX=0f
    private var startY=0f
    private var endX=0f
    private var endY=0f
    private var paint=Paint()


    @SuppressLint("LogNotTimber")
    private fun initView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        paint.isAntiAlias=true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX=w/2f
        centerY=h/2f
        startX=centerX-250
        startY=centerY
        endX=centerX+250
        endY=centerY
        eventX=centerX
        eventY=centerY-250
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = Color.GRAY
        //画3个点
        canvas.drawCircle(startX,startY,8f,paint)
        canvas.drawCircle(endX,endY,8f,paint)
        canvas.drawCircle(eventX,eventY,8f,paint)

        //绘制连线
        paint.strokeWidth=3f
        canvas.drawLine(startX,centerY,eventX,eventY,paint)
        canvas.drawLine(endX,centerY,eventX,eventY,paint)

        //画二阶的贝塞尔曲线
        paint.color=Color.GREEN
        paint.style=Paint.Style.STROKE
        var path=Path()
        path.moveTo(startX,startY)
        path.quadTo(eventX,eventY,endX,endY)
        canvas.drawPath(path,paint)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
        when(event.action){
            MotionEvent.ACTION_DOWN
            ->{
                eventX = event.x
                eventY = event.y
                invalidate()
            }
            MotionEvent.ACTION_MOVE->{
                eventX = event.x
                eventY = event.y
                invalidate()
            }
        }
        return true
    }
}