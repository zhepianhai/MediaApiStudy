package com.zph.media.view

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView


class MyTextureView : TextureView {
    private var ratioW = 0
    private var ratioH = 0


    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    /**
     * 设置宽高比
     * @param width
     * @param height
     */
    fun setAspectRation(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "width or height can not be negative." }
        //相机输出尺寸宽高默认是横向的，屏幕是竖向时需要反转
        // （后续适配屏幕旋转时会有更好的方案，这里先这样）
        ratioW = height
        ratioH = width
        //请求重新布局
        requestLayout()
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val height = MeasureSpec.makeMeasureSpec(
//            AndroidMediaApiActivity.TextureViewSize.getHeight(),
//            MeasureSpec.EXACTLY
//        )
//        val width = MeasureSpec.makeMeasureSpec(
//            BaseCameraProvider.TextureViewSize.getWidth(),
//            MeasureSpec.EXACTLY
//        )
//        var height = MeasureSpec.makeMeasureSpec(400, MeasureSpec.EXACTLY);
//        var width = MeasureSpec.makeMeasureSpec(1400, MeasureSpec.EXACTLY);
//        setMeasuredDimension(width, height)
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//
//        if (0 == ratioW || 0 == ratioH) {
//            //未设定宽高比，使用预览窗口默认宽高
//            setMeasuredDimension(width, height)
//        } else {
//            //设定宽高比，调整预览窗口大小（调整后窗口大小不超过默认值）
//            if (width < height * ratioW / ratioH) {
//                setMeasuredDimension(width, width * ratioH / ratioW)
//            } else {
//                setMeasuredDimension(height * ratioW / ratioH, height)
//            }
//        }


        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == ratioW || 0 == ratioH) {
            setMeasuredDimension(width, height)
        } else {
            //注意这里骚操作，替换"小于号"为"大于号"(用来处理全屏的操作)
            if (width > height * ratioW / ratioH) {
                setMeasuredDimension(width, width * ratioW / ratioH)
            } else {
                setMeasuredDimension(height * ratioW / ratioH, height)
            }
        }
    }
}