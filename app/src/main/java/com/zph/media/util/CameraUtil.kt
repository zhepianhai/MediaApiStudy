package com.zph.media.util

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.zph.media.view.MyTextureView
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class CameraUtil {
    companion object {
        //选择sizeMap中大于并且最接近width和height的size
        fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size? {
            val sizeList: MutableList<Size> = ArrayList()
            Log.i("TAGG", "原始width:$width")
            Log.i("TAGG", "原始height:$height")
            for (option in sizeMap) {
                Log.i("TAGG", "option-height:${option.height}")
                Log.i("TAGG", "option-width:${option.width}")

                if (width > height) {
                    if (option.width > width && option.height > height) {
                        sizeList.add(option)
                    }
                } else {
                    if (option.width > height && option.height > width) {
//                        var optionNew=Size(option.height,option.width)
                        sizeList.add(option)
                    }
                }
            }
            return if (sizeList.size > 0) {
                Collections.min(sizeList,
                    Comparator<Size?> { lhs, rhs -> java.lang.Long.signum((lhs!!.width * lhs.height - rhs!!.width * rhs.height).toLong()) })
            } else sizeMap[0]
        }


        fun setPreviewSize(@NotNull surfaceTexture: TextureView, cameraCharacteristics: CameraCharacteristics): Size {
            val aspectRatios = ArrayList<Float>()
            aspectRatios.add(16.toFloat() / 9)
            aspectRatios.add(4.toFloat() / 3)
            aspectRatios.add(18.toFloat() / 9)

            val size = getPreviewSize(cameraCharacteristics, aspectRatios)
            return size
        }
        /**
         * 获取预览尺寸
         * 参数2：预览尺寸比例的集合，按加入顺序寻找预览尺寸并返回
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getPreviewSize(@NotNull cameraCharacteristics: CameraCharacteristics, aspectRatios: ArrayList<Float>): Size {
            for (aspectRatio in aspectRatios) {
                val size = getPreviewSize(cameraCharacteristics, aspectRatio)
                if (size != null) {
                    return size
                }
            }
            return Size(1280, 720)
        }
        /**
         * 获取预览尺寸
         * 参数2：预览尺寸比例，如4:3，16:9
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getPreviewSize(@NotNull cameraCharacteristics: CameraCharacteristics, aspectRatio: Float): Size? {
            val streamConfigurationMap =
                cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val supportedSizes = streamConfigurationMap!!.getOutputSizes(SurfaceTexture::class.java)
            for (size in supportedSizes) {
                if (size.width.toFloat() / size.height == aspectRatio) {
                    return size
                }
            }
            return null
        }

    }
}