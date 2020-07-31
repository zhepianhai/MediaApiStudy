package com.zph.media.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.zph.media.R
import com.zph.media.extensions.dp

/**
 * @author zph
 * 自定义Button
 * 有长按动画
 * 可拖拽（注释了）
 * 可配置圆环颜色
 * 可配置触摸状态
 *
 * */
class TouchButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = TouchButton::class.java.simpleName

    private lateinit var mPaint: Paint
    private lateinit var mTextPaint: Paint

    private var mCenterX = 0f
    private var mCenterY: Float = 0f

    private var mRingRadius = 0f
    private var mRadius = 0f
    private var mAnimPadding = 0f

    private lateinit var mPath: Path

    private var mTouchDown = false
    private var mAnimatorValue = 0f

    private var mCurrentValue = 0f
    private var mDefaultStroke = 0f

    private var mText: String
    private var mTextWidth = 0f
    private var mTextSize = 0f

    private var mLastX = 0f
    private var mLastY: Float = 0f

    /**
     * parent view width and height
     */
    private var mParentWidth = 0

    /**
     * parent view width and height
     */
    private var mParentHeight: Int = 0

    /**
     * original position
     */
    private var mLeft = 0

    /**
     * original position
     */
    private var mTop: Int = 0

    /**
     * original position
     */
    private var mRight: Int = 0

    /**
     * original position
     */
    private var mBottom: Int = 0
    var mBtnBackground = 0
    var mBtnBackGroupD = 0
    var mTextColor = 0
    var mTextSizeDp = 16

    private var canTouch = true

    val valueAnimator = ValueAnimator.ofFloat(0.8f, -0.8f)

    //    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr){
//        initView(context,attributeSet,defStyleAttr)
//    }
    init {
        mBtnBackground = R.styleable.TouchButton_tb_color
        mBtnBackGroupD = R.styleable.TouchButton_tb_color_d
        mTextColor = R.styleable.TouchButton_tb_text_color
        mText = ""
        mAnimPadding = 10.dp.toFloat()

        initView(context, attrs, defStyleAttr)

    }
    companion object {
        val TYPE_PIC = 0
        val TYPE_VIDEO = 1
    }

    var typeBtn: Int=TYPE_VIDEO

    //更改颜色
    fun setColorType(type: Int) {
        typeBtn=type
        when (type) {
            TYPE_PIC -> {
                mBtnBackground = ContextCompat.getColor(context, R.color.colorWrite)
                mBtnBackGroupD = ContextCompat.getColor(context, R.color.colorWrite_d)
            }
            TYPE_VIDEO -> {
                mBtnBackground = ContextCompat.getColor(context, R.color.colorRed)
                mBtnBackGroupD = ContextCompat.getColor(context, R.color.colorRed_d)

            }
        }

        invalidate()
    }

    //设置是否可触摸
    public fun setCanTouch(flag: Boolean) {
        canTouch = flag
        invalidate()
    }

    @SuppressLint("LogNotTimber")
    private fun initView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {

        attrs?.let {
            val attributes =
                context.obtainStyledAttributes(attrs, R.styleable.TouchButton, defStyleAttr, 0)
            mBtnBackground = attributes.getColor(
                R.styleable.TouchButton_tb_color,
                ContextCompat.getColor(context, R.color.colorRed)
            )
            mBtnBackGroupD = attributes.getColor(
                R.styleable.TouchButton_tb_color_d,
                ContextCompat.getColor(context, R.color.colorRed_d)
            )

            mTextColor = attributes.getColor(
                R.styleable.TouchButton_tb_text_color,
                ContextCompat.getColor(context, android.R.color.white)
            )
            mText = ""
            mAnimPadding = attributes.getDimension(
                R.styleable.TouchButton_tb_anim_padding,
                10.dp.toFloat()
            )
            attributes.recycle()
        }

        mDefaultStroke = mAnimPadding / 2

        mCurrentValue = mDefaultStroke
        mPaint = Paint()
        mPaint?.let {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
            it.strokeWidth = mCurrentValue
            it.color = mBtnBackground
        }

        mTextSize = mTextSizeDp.dp.toFloat()
        mTextPaint = Paint()
        mTextPaint?.let {
            it.isAntiAlias = true
            it.color = mTextColor
            it.textSize = mTextSize
        }

        mPath = Path()
        valueAnimator.addUpdateListener {
            mAnimatorValue = valueAnimator.animatedValue as Float
            Log.d(TAG, "value:$mAnimatorValue")
            invalidate()
        }
        valueAnimator.duration = 900
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        mTextWidth = mTextPaint.measureText(mText)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width = 0
        var height = 0
        when (widthMode) {
            MeasureSpec.EXACTLY -> width = widthSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> width = 80.dp
        }
        when (heightMode) {
            MeasureSpec.EXACTLY -> height = heightSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> height = 80.dp
        }
        mRadius = width.coerceAtMost(height) / 2.toFloat()
        mRingRadius = mRadius + mAnimPadding
        width = (mRingRadius * 2 + mAnimPadding + mDefaultStroke * 1.5).toInt()
        height = (mRingRadius * 2 + mAnimPadding + mDefaultStroke * 1.5).toInt()
        mCenterX = width / 2.toFloat()
        mCenterY = height / 2.toFloat()
        Log.e(TAG, "width:$width,height:$height")
        setMeasuredDimension(width, height)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (!changed) {
            mLeft = left
            mTop = top
            mRight = right
            mBottom = bottom
        }
        val parent = parent
        if (parent is ViewGroup) {
            mParentWidth = parent.measuredWidth
            mParentHeight = parent.measuredHeight
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mTouchDown) {
            drawRing(canvas)
        } else {
            drawCircle(canvas)
        }
    }

    private fun drawRing(canvas: Canvas) {
        mPath.reset()
        mPaint.color = mBtnBackground
        mPath.addCircle(mCenterX, mCenterY, mRingRadius, Path.Direction.CW)
        mPaint.style = Paint.Style.STROKE
        mCurrentValue += mAnimatorValue
        Log.d(TAG, "current:$mCurrentValue")
        mPaint.strokeWidth = mCurrentValue
        canvas.drawPath(mPath, mPaint)
    }

    private fun drawCircle(canvas: Canvas) {
        mPath.reset()

        mPaint?.let {
            // 绘制圆环
            var center = width / 2f
            var innerCircle: Float = 30.dp.toFloat() //内圆半径
            var ringWidth: Float = 4.dp.toFloat()//圆环宽度
            it.style = Paint.Style.FILL
            it.color = mBtnBackground
            canvas.drawCircle(center, center, innerCircle, it)
            it.color = mBtnBackGroupD

            it.style = Paint.Style.STROKE
            it.color = Color.TRANSPARENT
            canvas.drawCircle(center, center, innerCircle + 4, it)

            it.color = mBtnBackGroupD
            it.style = Paint.Style.STROKE
            canvas.drawCircle(center, center, innerCircle + 12, it)


        }

        //draw text
        val x = (measuredWidth - mTextWidth) / 2
        val y: Float = mCenterY + mTextSize / 3
        canvas.drawText(mText, x, y, mTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!canTouch) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.rawX
                mLastY = event.rawY
                mTouchDown = true
                valueAnimator.start()
                postInvalidate()
                if (mOnHoldListener != null) {
                    mOnHoldListener!!.onHold(true)
                }

                return true
            }
//            MotionEvent.ACTION_MOVE -> {
//                val dx = (event.rawX - mLastX).toInt()
//                val dy = (event.rawY - mLastY).toInt()
//                var l = left + dx
//                var b = bottom + dy
//                var r = right + dx
//                var t = top + dy
//                // 下面判断移动是否超出屏幕
//                if (l < 0) {
//                    l = 0
//                    r = l + width
//                }
//                if (t < 0) {
//                    t = 0
//                    b = t + height
//                }
//                if (r > mParentWidth) {
//                    r = mParentWidth
//                    l = r - width
//                }
//                if (b > mParentHeight) {
//                    b = mParentHeight
//                    t = b - height
//                }
//                layout(l, t, r, b)
//                mLastX = event.rawX
//                mLastY = event.rawY
//                postInvalidate()
//                return true
//            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mTouchDown = false
                mCurrentValue = mDefaultStroke
                valueAnimator.cancel()
                //                layout(mLeft, mTop, mRight, mBottom);
                postInvalidate()
                if (mOnHoldListener != null) {
                    mOnHoldListener!!.onHold(false)
                }
                return true
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }


    private var mOnHoldListener: OnHoldListener? = null

    fun setOnHoldListener(listener: OnHoldListener?) {
        mOnHoldListener = listener
    }

    interface OnHoldListener {
        fun onHold(hold: Boolean)
    }


}