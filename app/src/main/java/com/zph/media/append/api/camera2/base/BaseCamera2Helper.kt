package com.zph.media.append.api.camera2.base

import android.app.Activity
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import java.util.concurrent.Semaphore

abstract class BaseCamera2Helper() {

    abstract fun onResume()
    abstract fun onPause()
    abstract fun takePhoto()
    abstract fun takeVideo()
    abstract fun setImagPath(path:String)
    abstract fun setMp4Path(path:String)


    protected val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    protected val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    protected val DEFAULT_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }
    protected val INVERSE_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 270)
        append(Surface.ROTATION_90, 180)
        append(Surface.ROTATION_180, 90)
        append(Surface.ROTATION_270, 0)
    }


    /**
     *  是一个连接的相机设备代表 [android.hardware.camera2.CameraDevice].
     */
    protected var cameraDevice: CameraDevice? = null

    /**
     * 是一个事务，用来向相机设备发送获取图像的请求 [android.hardware.camera2.CameraCaptureSession]
     */
    protected var captureSession: CameraCaptureSession? = null

    /**
     *  预览尺寸大小. [android.util.Size]
     */
    protected lateinit var previewSize: Size

    /**
     *  录制尺寸大小.[android.util.Size]
     */
    protected lateinit var videoSize: Size

    /**
     * 是否正在录制视频
     */
    protected var isRecordingVideo = false

    /**
     * 子线程
     */
    protected var backgroundThread: HandlerThread? = null
    protected var backgroundHandler: Handler? = null

    /**
     *相机锁
     */
    protected val cameraOpenCloseLock = Semaphore(1)

    /**
     * 一个捕捉的请求。我们可以为不同的场景（预览、拍照）创建不同的捕捉请求，并可以配置不同的捕捉属性，如：预览分辨率，预览目标，对焦模式、曝光模式等等[CaptureRequest.Builder]
     */
    protected lateinit var previewRequestBuilder: CaptureRequest.Builder

    /**
     * 摄像头传感器的方向
     */
    protected var sensorOrientation = 0

    /**
     * 文件输出的地址和录屏
     */
    protected var nextVideoAbsolutePath: String? = null

    /**
     * 视频录制类
     * */
    protected var mediaRecorder: MediaRecorder? = null

    /**
     * 图片获取
     * */
    protected var mImageReader: ImageReader? = null

    /**
     * 图片路径
     * */
    protected var imgPath:String=""

    /**
     * 视频路径
     * */
    protected var videoPath:String=""

}


