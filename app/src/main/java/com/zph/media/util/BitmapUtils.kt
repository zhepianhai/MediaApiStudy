package com.zph.media.util

import android.content.res.Resources
import android.graphics.*
import android.os.Environment
import android.util.Log
import android.util.Size
import com.zph.media.config.Constants
import okio.buffer
import okio.sink
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


object BitmapUtils {
    fun toByteArray(bitmap: Bitmap): ByteArray {
        var os = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        return os.toByteArray()
    }

    fun mirror(rawBitmap: Bitmap): Bitmap {
        var matrix = Matrix()
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
    }

    fun rotate(rawBitmap: Bitmap, degree: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
    }

    fun decodeBitmap(bitmap: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        var bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size(), options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size(), options)
    }

    fun decodeBitmapFromFile(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, options)
    }

    fun decodeBitmapFromResource(
        res: Resources,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val rawWidth = options.outWidth
        val rawHeight = options.outHeight
        var inSampleSize = 1

        if (rawWidth > reqWidth || rawHeight > reqHeight) {
            val halfWidth = rawWidth / 2
            val halfHeight = rawHeight / 2

            while ((halfWidth / inSampleSize) > reqWidth && (halfHeight / inSampleSize) > reqHeight) {
                inSampleSize *= 2  //设置inSampleSize为2的幂是因为解码器最终还是会对非2的幂的数进行向下处理，获取到最靠近2的幂的数
            }
        }
        return inSampleSize
    }

    fun savePic(
        data: ByteArray,
        isMirror: Boolean = false,
        onSuccess: (savedPath: String, time: String) -> Unit,
        onFailed: (msg: String) -> Unit
    ) {
        thread {
            try {
                val temp = System.currentTimeMillis()
                var path =
                    Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_IMAGE_FILE_PATH
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                var picFile = File(path, timeStamp + "test.jpg")
                if (picFile != null && data != null) {
                    val rawBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    //
                    val resultBitmap = if (isMirror) rotate(rawBitmap, 90.0f) else rawBitmap
                    picFile.sink().buffer().write(toByteArray(resultBitmap)).close()
                    onSuccess("${picFile.absolutePath}", "${System.currentTimeMillis() - temp}")

                    Log.i(
                        "TAGG",
                        "图片已保存! 耗时：${System.currentTimeMillis() - temp}    路径：  ${picFile.absolutePath}"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailed("${e.message}")
            }
        }
    }

    /**
     * 预览数据保存
     * @param y 预览数据，Y分量
     * @param u 预览数据，U分量
     * @param v 预览数据，V分量
     * @param previewSize  预览尺寸
     * @param stride    步长
     */
    fun saveYuv420_422(
        y: ByteArray, u: ByteArray, v: ByteArray,
        previewSize: Size,
        stride: Int,
        onSuccess: (savedPath: String, time: String) -> Unit,
        onFailed: (msg: String) -> Unit
    ) {
        thread {
            var nv21 = ByteArray(stride * previewSize.height * 3 / 2)
            //回传的是YUV422
            if (y.size / u.size == 2) {
                ImageUtil.yuv422ToYuv420sp(y, u, v, nv21, stride, previewSize.height)
            } else if (y.size / u.size == 4) {
                ImageUtil.yuv420ToYuv420sp(y, u, v, nv21, stride, previewSize.height)
            }
            var yuvImage = YuvImage(nv21, ImageFormat.NV21, stride, previewSize.height, null)
            // ByteArrayOutputStream的close中其实没做任何操作，可不执行
            var byteArrayOutputStream = ByteArrayOutputStream()
            // 由于某些stride和previewWidth差距大的分辨率，
            // [0,previewWidth)是有数据的，而[previewWidth,stride)补上的U、V均为0，因此在这种情况下运行会看到明显的绿边
//            yuvImage.compressToJpeg(
//                Rect(0, 0, stride, previewSize.height),
//                100,
//                byteArrayOutputStream
//            )
// 由于U和V一般都有缺损，因此若使用方式，可能会有个宽度为1像素的绿边
            yuvImage.compressToJpeg(
                Rect(0, 0, previewSize.width, previewSize.height),
                100,
                byteArrayOutputStream
            )
            var jpgBytes = byteArrayOutputStream.toByteArray()
            var options = BitmapFactory.Options()
            options.inSampleSize=4
            // 原始预览数据生成的bitmap
            var originalBitmap = BitmapFactory.decodeByteArray(jpgBytes, 0, jpgBytes.size, options)
            var matrix = Matrix()
            // 预览相对于原数据可能有旋转
//            matrix.postRotate(if (Camera2Helper.CAMERA_ID_BACK.equals(openedCameraId)) displayOrientation else -displayOrientation)
            // 对于前置数据，镜像处理；若手动设置镜像预览，则镜像处理；若都有，则不需要镜像处理

            // 对于前置数据，镜像处理；若手动设置镜像预览，则镜像处理；若都有，则不需要镜像处理
//            if (Camera2Helper.CAMERA_ID_FRONT.equals(openedCameraId) xor isMirrorPreview) {
//                matrix.postScale(-1f, 1f)
//            }
            // 和预览画面相同的bitmap
            var previewBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);

        }
    }
}