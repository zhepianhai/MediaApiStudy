package com.zph.media.util

object ImageUtil {

    /**
     * 将Y:U:V == 4:2:2的数据转换为nv21
     *
     * @param y      Y 数据
     * @param u      U 数据
     * @param v      V 数据
     * @param nv21   生成的nv21，需要预先分配内存
     * @param stride 步长
     * @param height 图像高度
     */
    open fun yuv422ToYuv420sp(
        y: ByteArray,
        u: ByteArray,
        v: ByteArray,
        nv21: ByteArray,
        stride: Int,
        height: Int
    ): Unit {
        System.arraycopy(y, 0, nv21, 0, y.size)
        // 注意，若length值为 y.length * 3 / 2 会有数组越界的风险，需使用真实数据长度计算
        val length = y.size + u.size / 2 + v.size / 2
        var uIndex = 0
        var vIndex = 0
        var i = stride * height
        while (i < length) {
            nv21[i] = v[vIndex]
            nv21[i + 1] = u[uIndex]
            vIndex += 2
            uIndex += 2
            i += 2
        }
    }


    /**
     * 将Y:U:V == 4:1:1的数据转换为nv21
     *
     * @param y      Y 数据
     * @param u      U 数据
     * @param v      V 数据
     * @param nv21   生成的nv21，需要预先分配内存
     * @param stride 步长
     * @param height 图像高度
     */
    fun yuv420ToYuv420sp(
        y: ByteArray,
        u: ByteArray,
        v: ByteArray,
        nv21: ByteArray,
        stride: Int,
        height: Int
    ) {
        System.arraycopy(y, 0, nv21, 0, y.size)
        // 注意，若length值为 y.length * 3 / 2 会有数组越界的风险，需使用真实数据长度计算
        val length = y.size + u.size + v.size
        var vIndex = 0
        for ((uIndex, i) in (stride * height until length).withIndex()) {
            nv21[i] = v[vIndex++]
            nv21[i + 1] = u[uIndex]
        }
    }

}