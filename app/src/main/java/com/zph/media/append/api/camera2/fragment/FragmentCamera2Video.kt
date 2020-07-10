package com.zph.media.append.api.camera2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import com.zph.media.R
import com.zph.media.config.Constants
import com.zph.media.util.BitmapUtils
import com.zph.media.util.CameraUtil
import com.zph.media.util.ToastUtil
import com.zph.media.util.ToastUtil.Companion.showToast
import kotlinx.android.synthetic.main.activity_android_media_api.*
import kotlinx.android.synthetic.main.fragment_camera2_video2.*
import kotlinx.android.synthetic.main.fragment_camera2_video2.btn_take_pic
import org.jetbrains.anko.support.v4.runOnUiThread
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 *视频录制的完整例子
 */
class FragmentCamera2Video : Fragment() {
    companion object {
        fun newInstance(): FragmentCamera2Video =
            FragmentCamera2Video()
    }

    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    private val DEFAULT_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }
    private val INVERSE_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 270)
        append(Surface.ROTATION_90, 180)
        append(Surface.ROTATION_180, 90)
        append(Surface.ROTATION_270, 0)
    }

    /**
     *  是一个连接的相机设备代表 [android.hardware.camera2.CameraDevice].
     */
    private var cameraDevice: CameraDevice? = null

    /**
     * 是一个事务，用来向相机设备发送获取图像的请求 [android.hardware.camera2.CameraCaptureSession]
     */
    private var captureSession: CameraCaptureSession? = null

    /**
     *  预览尺寸大小. [android.util.Size]
     */
    private lateinit var previewSize: Size

    /**
     *  录制尺寸大小.[android.util.Size]
     */
    private lateinit var videoSize: Size

    /**
     * 是否正在录制视频
     */
    private var isRecordingVideo = false

    /**
     * 子线程
     */
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    /**
     *相机锁
     */
    private val cameraOpenCloseLock = Semaphore(1)

    /**
     * 一个捕捉的请求。我们可以为不同的场景（预览、拍照）创建不同的捕捉请求，并可以配置不同的捕捉属性，如：预览分辨率，预览目标，对焦模式、曝光模式等等[CaptureRequest.Builder]
     */
    private lateinit var previewRequestBuilder: CaptureRequest.Builder

    /**
     * 摄像头传感器的方向
     */
    private var sensorOrientation = 0

    /**
     * 文件输出的地址和录屏
     */
    private var nextVideoAbsolutePath: String? = null

    /**
     * 视频录制类
     * */
    private var mediaRecorder: MediaRecorder? = null

    /**
     * 图片获取
     * */
    private  var mImageReader: ImageReader? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera2_video2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_record_view.setOnClickListener {
            //视频录制
            if (isRecordingVideo) stopRecordingVideo() else startRecordingVideo()
        }
        btn_take_pic.setOnClickListener {
            //拍照
            if(mImageReader==null) initImageReader()
            takePicture()
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable) {
            openCamera(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    //Tries to open a [CameraDevice]. The result is listened by [stateCallback].
    @SuppressLint("MissingPermission")
    private fun openCamera(width: Int, height: Int) {
        val cameraActivity = activity
        if (cameraActivity == null || cameraActivity.isFinishing) return
        val manager = cameraActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            val cameraId = manager.cameraIdList[0]
            //找到支持的最佳屏幕分辨率
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?: throw RuntimeException("找不到可用的最佳分辨率")
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
            videoSize = CameraUtil.chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            previewSize = CameraUtil.chooseOptimalSize(
                map.getOutputSizes(SurfaceTexture::class.java),
                width, height, videoSize
            )
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(previewSize.width, previewSize.height)
            } else {
                mTextureView.setAspectRatio(previewSize.height, previewSize.width)
            }
            configureTransform(width, height)
            mediaRecorder = MediaRecorder()
            initImageReader()
            manager.openCamera(cameraId, stateCallback, null)

        } catch (e: CameraAccessException) {
            showToast(activity, "Cannot access the camera.")
            cameraActivity.finish()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.")
        }

    }

    /**
     * 开启相机预览.
     */
    private fun startPreview() {
        if (cameraDevice == null || !mTextureView.isAvailable) return
        try {
            closePreviewSession()
            if(mImageReader==null||mImageReader?.surface==null) initImageReader()
            val texture = mTextureView.surfaceTexture
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)
            previewRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            val previewSurface = Surface(texture)
            previewRequestBuilder.addTarget(previewSurface)
            val surfaceList = ArrayList<Surface>().apply {
                add(previewSurface)
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
                            showToast(activity, "Failed")
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
     * textureView的一些配置更改，在openCamera中确定相机预览大小之前，不应调用此方法
     * 或者直到“textureView”的大小固定
     * */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = (activity as FragmentActivity).windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale =
                (viewHeight.toFloat() / previewSize.height).coerceAtLeast(viewWidth.toFloat() / previewSize.width)
            with(matrix) {
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        }
        mTextureView.setTransform(matrix)
    }

    /**
     * 视频录制，开始录制
     * */
    private fun startRecordingVideo() {
        if (cameraDevice == null || !mTextureView.isAvailable) return
        try {
            closePreviewSession()
            setUpMediaRecorder()//配置录制相关类
            val texture = mTextureView.surfaceTexture.apply {
                setDefaultBufferSize(previewSize.width, previewSize.height)
            }
            //为相机预览和MediaRecorder设置Surface
            val previewSurface = Surface(texture)
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
                            btn_record_view.text = "停止录制"
                            isRecordingVideo = true
                            mediaRecorder?.start()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        if (activity != null) showToast(activity, "Failed")
                    }
                }, backgroundHandler
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 视频录制，停止录制
     * */
    private fun stopRecordingVideo() {
        isRecordingVideo = false
        btn_record_view.text = "开始录制"
        mediaRecorder?.apply {
            stop()
            reset()
        }

        if (activity != null) showToast(activity, "Video saved: $nextVideoAbsolutePath")
        nextVideoAbsolutePath = null
        startPreview()
    }

    /**
     * 获取图片的初始化
     * */
    private fun initImageReader() {
        mImageReader =
            ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
        mImageReader?.setOnImageAvailableListener({
            val image = it.acquireNextImage()
            val byteBuffer = image.planes[0].buffer
            val byteArray = ByteArray(byteBuffer.remaining())
            byteBuffer.get(byteArray)
//            it.close()
            BitmapUtils.savePic(byteArray, true, { savedPath, time ->
                this.runOnUiThread {
                    if(activity!=null) {
                        showToast(activity, "图片保存成功！ 保存路径：$savedPath 耗时：$time")
                        startPreview()
                    }
                }
            }, { msg ->
                this.runOnUiThread {
                    Log.i("TAGG", "错误: $msg")
                    showToast(activity,  "图片保存失败：$msg")
                    startPreview()
                }
            })

        },backgroundHandler )
    }
    /**
     * 拍照相关业务
     * */
    private fun takePicture(){
        if (cameraDevice != null && mTextureView.isAvailable&&activity!=null) {
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
                    ?: showToast(activity, "拍照异常")
            }

        }

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
        val filename = "${System.currentTimeMillis()}.mp4"

        var dir =
            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_MP4_FILE_PATH

        return "$dir/$filename"
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
            mImageReader=null
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

    /**
     * 一些Camera2监听回调
     * */

    //TextureView.SurfaceTextureListener
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture) = true
        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }

    }

    //连接的相机设备 回调相机状态发生变化时
    //以用于接收相机状态的更新和后续的处理。
    private val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            //当相机打开成功之后会回调此方法
            // 一般在此进行获取一个全局的CameraDevice实例，开启相机预览等操作
            cameraOpenCloseLock.release()
            this@FragmentCamera2Video.cameraDevice = cameraDevice
            startPreview()
            configureTransform(mTextureView.width, mTextureView.height)
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            //相机设备失去连接(不能继续使用)时回调此方法，同时当打开相机失败时也会调用此方法而不会调用onOpened()
            // 可在此关闭相机，清除CameraDevice引用
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@FragmentCamera2Video.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            //相机发生错误时调用此方法
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@FragmentCamera2Video.cameraDevice = null
            activity?.finish()
        }

        override fun onClosed(camera: CameraDevice) {
            //相机完全关闭时回调此方法
            super.onClosed(camera)
        }
    }
}
