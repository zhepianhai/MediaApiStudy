package com.zph.media.append.api.camera2.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.zph.media.config.Constants
import com.zph.media.util.BitmapUtils
import com.zph.media.util.CameraUtil
import com.zph.media.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_camera2_surface_view.*
import kotlinx.android.synthetic.main.fragment_camera2_video2.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


/**
 * Camera2 封装
 * 基于 SurfaceView
 * @OPen
 * 有拍照功能
 * 录像功能
 * closeCamera
 * */
class Camera2SurfaceHelper(private val activity: Activity, private val surfaceView:SurfaceView) : BaseCamera2Helper() {

    //surfaceView 的holder
    private lateinit var mSurfaceHolder: SurfaceHolder

    override fun onResume(){
        startBackgroundThread()
        mSurfaceHolder = surfaceView.holder
        mSurfaceHolder.setKeepScreenOn(true)
        mSurfaceHolder.addCallback(surfaceState)
    }
    override fun onPause(){
        closeCamera()
        stopBackgroundThread()
    }
    override fun takePhoto(){
        if (mImageReader == null) initImageReader()
        takePicture()
    }

    override fun takeVideo() {
        if (isRecordingVideo) stopRecordingVideo() else startRecordingVideo()
    }

    override fun setImagPath(path: String) {
        imgPath=path
    }

    override fun setMp4Path(path: String) {
        videoPath=path
    }


    /**
     * 拍照相关业务
     * */
    private fun takePicture() {
        if (cameraDevice != null && activity != null) {
            cameraDevice?.apply {
                val captureRequestBuilder =
                    createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder.addTarget(mImageReader?.surface!!)
                val rotation = activity?.windowManager?.defaultDisplay?.rotation

                captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                ) // 自动对焦
                captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                )     // 闪光灯
                captureRequestBuilder.set(
                    CaptureRequest.JPEG_ORIENTATION,
                    rotation
                )      //根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
                captureSession?.capture(
                    captureRequestBuilder.build(),
                    null,
                    backgroundHandler
                )
                    ?: ToastUtil.showToast(activity, "拍照异常")
            }

        }

    }
    /**
     * 录像相关业务
     * */
    /**
     * 视频录制，开始录制
     * */
    private fun startRecordingVideo() {
        if (cameraDevice == null) return
        try {
            closePreviewSession()
            setUpMediaRecorder()//配置录制相关类
            //为相机预览和MediaRecorder设置Surface
            val previewSurface =mSurfaceHolder.surface
            val recorderSurface = mediaRecorder!!.surface
            val surfaces = ArrayList<Surface>().apply {
                add(previewSurface)
                add(recorderSurface)
                mImageReader?.surface?.let { add(it) }
            }
            previewRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
                    addTarget(previewSurface)
                    addTarget(recorderSurface)
                }
            // 启动捕获会话
            // 一旦会话开始，我们就可以更新UI并开始录制
            cameraDevice?.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        captureSession = cameraCaptureSession
                        updatePreview()
                        activity?.runOnUiThread {
                            isRecordingVideo = true
                            mediaRecorder?.start()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        if (activity != null) ToastUtil.showToast(activity, "Failed")
                    }
                }, backgroundHandler
            )

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 视频录制，停止录制
     * */
    private fun stopRecordingVideo() {
        isRecordingVideo = false
        mediaRecorder?.apply {
            stop()
            reset()
        }
        impl?.let {
            nextVideoAbsolutePath?.let { it1 -> impl.camera2HelperVideoImpl(it1) }
        }
        if (activity != null) ToastUtil.showToast(activity, "Video saved: $nextVideoAbsolutePath")
        nextVideoAbsolutePath = null
        startPreview()
    }
    /**
     * 配置录制相关参数
     * */
    @Throws(IOException::class)
    private fun setUpMediaRecorder() {
        val cameraActivity = activity ?: return
        if (nextVideoAbsolutePath.isNullOrEmpty()) {
            nextVideoAbsolutePath = getVideoFilePath(cameraActivity)
        }
        val rotation = cameraActivity.windowManager.defaultDisplay.rotation
        when (sensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES ->
                mediaRecorder?.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation))
            SENSOR_ORIENTATION_INVERSE_DEGREES ->
                mediaRecorder?.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation))
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)//设置音频输入源
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(nextVideoAbsolutePath)
            setVideoEncodingBitRate(10000000)//设置编码比特率,不设置会使视频图像模糊
            setVideoFrameRate(30)//设置所录制视频的编码位率
            setVideoSize(videoSize.width, videoSize.height)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)//设置视频的编码格式
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)//设置音频的编码格式
            prepare()
        }
    }
    private fun getVideoFilePath(context: Context?): String {
        if(videoPath.isEmpty()) {
            val filename = "${System.currentTimeMillis()}.mp4"
            var dir =
                Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_MP4_FILE_PATH

            return "$dir/$filename"
        }
        return videoPath
    }
    private fun startBackgroundThread(){
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    //打开
    @SuppressLint("MissingPermission")
    private fun openCamera(){
        val cameraActivity = activity
        if (cameraActivity == null || cameraActivity.isFinishing) return
        val manager = cameraActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw java.lang.RuntimeException("Time out waiting to lock camera opening,")
            }
            val cameraId = manager.cameraIdList[0]
            //找到最佳分辨率
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?: throw java.lang.RuntimeException("没有找到最佳分辨率")
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
            videoSize = CameraUtil.chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))

            previewSize = CameraUtil.chooseOptimalSize(
                map.getOutputSizes(MediaRecorder::class.java),
                surfaceView.width,
                surfaceView.height,
                videoSize
            )

            mediaRecorder = MediaRecorder()
            initImageReader()
            manager.openCamera(cameraId, stateCallback, null)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /**
     * 开启相机预览.
     */
    private fun startPreview() {
        if (cameraDevice == null) return
        try {
            Log.i("TAG","startPreview")
            closePreviewSession()
            if (mImageReader == null || mImageReader?.surface == null) initImageReader()
//            texture.(previewSize.width, previewSize.height)
            previewRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget( mSurfaceHolder.surface)
            previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON);
            val surfaceList = ArrayList<Surface>().apply {
                add( mSurfaceHolder.surface)
                mImageReader?.surface?.let { add(it) }
            }
            cameraDevice?.createCaptureSession(
                surfaceList,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        if (activity != null)
                            ToastUtil.showToast(activity, "Failed")
                    }
                }, backgroundHandler
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 更新相机画面.
     */
    private fun updatePreview() {
        if (cameraDevice == null) return

        try {
            setUpCaptureRequestBuilder(previewRequestBuilder)
            HandlerThread("CameraPreview").start()
            captureSession?.setRepeatingRequest(
                previewRequestBuilder.build(),
                null, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e("TAG", e.toString())
        }

    }

    private fun setUpCaptureRequestBuilder(builder: CaptureRequest.Builder?) {
        builder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
    }
    /**
     * 关闭 处理
     * */
    private fun closePreviewSession() {
        captureSession?.close()
        captureSession = null
    }

    /**
     * Close the [CameraDevice].
     */
    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            closePreviewSession()
            cameraDevice?.close()
            cameraDevice = null
            mediaRecorder?.release()
            mediaRecorder = null
            mImageReader?.close()
            mImageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    /**
     * 停止线程和Hanler.
     */
    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e("TAG", e.toString())
        }
    }


    private fun initImageReader() {
        mImageReader =
            ImageReader.newInstance(
                previewSize.width,
                previewSize.height,
                ImageFormat.YUV_420_888,
                2
            )
        mImageReader?.setOnImageAvailableListener({
            val image = it.acquireNextImage()
            // Y:U:V == 4:2:2
            var y: ByteArray? = null
            var u: ByteArray? = null
            var v: ByteArray? = null
            val lock = ReentrantLock()
            if (image.format == ImageFormat.YUV_420_888) {
                val planes: Array<Image.Plane> = image.planes
                // 加锁确保y、u、v来源于同一个Image
                lock.lock()
                if (y == null) {
                    y = ByteArray(planes[0].buffer.limit() - planes[0].buffer.position())
                    u = ByteArray(planes[1].buffer.limit() - planes[1].buffer.position())
                    v = ByteArray(planes[2].buffer.limit() - planes[2].buffer.position())
                }
                if (image.planes[0].buffer.remaining() == y.size) {
                    planes[0].buffer.get(y)
                    planes[1].buffer.get(u)
                    planes[2].buffer.get(v)
                    /**
                     * 预览数据回调
                     * @param y 预览数据，Y分量
                     * @param u 预览数据，U分量
                     * @param v 预览数据，V分量
                     * @param previewSize  预览尺寸
                     * @param stride    步长
                     */
                    BitmapUtils.saveYuv420_422(y, u!!, v!!, previewSize, planes[0].rowStride,
                        { savedPath, time ->
                            activity.runOnUiThread {
                                if (activity != null) {
                                    ToastUtil.showToast(
                                        activity,
                                        "图片保存成功！ 保存路径：$savedPath 耗时：$time"
                                    )
                                    impl?.let {
                                        impl.camera2HelperImageImpl(savedPath)
                                    }
                                    startPreview()
                                }
                            }
                        }, { msg ->
                            activity.runOnUiThread {
                                Log.i("TAGG", "错误: $msg")
                                ToastUtil.showToast(activity, "图片保存失败：$msg")
                                startPreview()
                            }
                        })
                }

                lock.unlock()
                image.close()
            }
        }, backgroundHandler)
    }


    //SurfaceView 状态初始回调
    private val surfaceState=object :SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            openCamera()
        }

    }
    //连接的相机设备 回调相机状态发生变化时
    //以用于接收相机状态的更新和后续的处理。
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice1: CameraDevice) {
            //当相机打开成功之后会回调此方法
            // 一般在此进行获取一个全局的CameraDevice实例，开启相机预览等操作
            cameraOpenCloseLock.release()
            cameraDevice = cameraDevice1
            Log.i("TAG","stateCallback:onOpened")
            startPreview()
        }

        override fun onDisconnected(cameraDevice1: CameraDevice) {
            //相机设备失去连接(不能继续使用)时回调此方法，同时当打开相机失败时也会调用此方法而不会调用onOpened()
            // 可在此关闭相机，清除CameraDevice引用
            cameraOpenCloseLock.release()
            cameraDevice1.close()
            cameraDevice = null
        }

        override fun onError(cameraDevice1: CameraDevice, error: Int) {
            //相机发生错误时调用此方法
            cameraOpenCloseLock.release()
            cameraDevice1.close()
            cameraDevice = null
            activity?.finish()
        }

    }
    open fun setCamera2HelpImpl(impl:Camera2HelpImp){
        this.impl=impl
    }
    private lateinit var impl:Camera2HelpImp
    interface Camera2HelpImp{
        fun camera2HelperImageImpl(path:String)

        fun camera2HelperVideoImpl(path:String)
    }

}