package com.zph.media.util

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.zph.media.append.api.camera2.CompareSizesByArea
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.abs

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
                sizeList.add(option)
//                if (width > height) {
//                    if (option.width > width && option.height > height) {
//                        sizeList.add(option)
//                    }
//                } else {
//                    if (option.width > height && option.height > width) {
////                        var optionNew=Size(option.height,option.width)
//                        sizeList.add(option)
//                    }
//                }
            }
            return if (sizeList.size > 0) {
                Collections.max(sizeList,
                    Comparator<Size?> { lhs, rhs -> java.lang.Long.signum((lhs!!.width * lhs.height - rhs!!.width * rhs.height).toLong()) })
            } else sizeMap[0]
        }


        fun setPreviewSize(
            @NotNull surfaceTexture: TextureView,
            cameraCharacteristics: CameraCharacteristics
        ): Size {
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
        fun getPreviewSize(
            @NotNull cameraCharacteristics: CameraCharacteristics,
            aspectRatios: ArrayList<Float>
        ): Size {
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
        fun getPreviewSize(
            @NotNull cameraCharacteristics: CameraCharacteristics,
            aspectRatio: Float
        ): Size? {
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


        fun getOptimalSize1(availablePreviewSizes: Array<Size>, width: Int, height: Int): Size? {
            var mCameraPreviewWidth = 0
            var mCameraPreviewHeight = 0
            var bestPreviewWidth = 0
            var bestPreviewHeight = 0
            var diffs: Int = Int.MAX_VALUE
            Log.i("TAGG", "原始width:$width")
            Log.i("TAGG", "原始height:$height")
            for (previewSize in availablePreviewSizes) {
                mCameraPreviewWidth = previewSize.width
                mCameraPreviewHeight = previewSize.height
                Log.i("TAGG", "option-width:${mCameraPreviewWidth}")
                Log.i("TAGG", "option-height:${mCameraPreviewHeight}")

                val newDiffs: Int =
                    abs(mCameraPreviewWidth - width) + abs(
                        mCameraPreviewHeight - height
                    )
                Log.i("TAGG", "newDiffs:${newDiffs}")
                if (newDiffs == 0) {
                    bestPreviewWidth = mCameraPreviewWidth
                    bestPreviewHeight = mCameraPreviewHeight
                    break
                }
                if (diffs > newDiffs) {
                    bestPreviewWidth = mCameraPreviewWidth
                    bestPreviewHeight = mCameraPreviewHeight
                    diffs = newDiffs
                }

            }
            return Size(bestPreviewWidth, bestPreviewHeight)
        }



        fun chooseOptimalSize(
            choices: Array<Size>,
            width: Int,
            height: Int,
            aspectRatio: Size
        ): Size {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val w = aspectRatio.width
            val h = aspectRatio.height
            val bigEnough = choices.filter {
                it.height == it.width * h / w && it.width >= width && it.height >= height }

            // Pick the smallest of those, assuming we found any
            return if (bigEnough.isNotEmpty()) {
                Collections.min(bigEnough, CompareSizesByArea())
            } else {
                choices[0]
            }
        }

         fun chooseVideoSize(choices: Array<Size>) = choices.firstOrNull {
            it.width == it.height * 4 / 3 && it.width <= 1080 } ?: choices[choices.size - 1]

    }
}