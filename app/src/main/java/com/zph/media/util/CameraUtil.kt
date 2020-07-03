package com.zph.media.util

import android.util.Size
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class CameraUtil {
    companion object {
        //选择sizeMap中大于并且最接近width和height的size
        fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size? {
            val sizeList: MutableList<Size> = ArrayList()
            for (option in sizeMap) {
                if (width > height) {
                    if (option.width > width && option.height > height) {
                        sizeList.add(option)
                    }
                } else {
                    if (option.width > height && option.height > width) {
                        sizeList.add(option)
                    }
                }
            }
            return if (sizeList.size > 0) {
                Collections.min(sizeList,
                    Comparator<Size?> { lhs, rhs -> java.lang.Long.signum((lhs!!.width * lhs.height - rhs!!.width * rhs.height).toLong()) })
            } else sizeMap[0]
        }
    }
}