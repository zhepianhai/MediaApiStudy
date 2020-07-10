package com.zph.media.append.api.camera2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zph.media.R
import com.zph.media.util.CameraUtil
import com.zph.media.util.ToastUtil.Companion.showToast
import kotlinx.android.synthetic.main.fragment_camera2_n_v21.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * 获取Camera2的 NV21数据
 * 预览数据：YUV_420_888
 */
class FragmentCamera2NV21 : Fragment() {
    companion object {
        fun newInstance(): FragmentCamera2NV21 =
            FragmentCamera2NV21()
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera2_n_v21, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_take_pic.setOnClickListener {
            //拍照
            if(mImageReader==null) initImageReader()
//            takePicture()
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
    @SuppressLint("MissingPermission")
    private fun openCamera(width: Int, height: Int) {
        val cameraActivity = activity
        if (cameraActivity == null || cameraActivity.isFinishing) return
        val manager = cameraActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if(!cameraOpenCloseLock.tryAcquire(2500,TimeUnit.MILLISECONDS)){
                throw java.lang.RuntimeException("Time out waiting to lock camera opening,")
            }
            val cameraId=manager.cameraIdList[0]
            //找到最佳分辨率
            val characteristics=manager.getCameraCharacteristics(cameraId)
            val map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?: throw java.lang.RuntimeException("没有找到最佳分辨率")
            sensorOrientation=characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
            videoSize=CameraUtil.chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            previewSize=CameraUtil.chooseOptimalSize(map.getOutputSizes(MediaRecorder::class.java),width,height,videoSize)
            if(resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE){
                mTextureView.setAspectRatio(previewSize.width,previewSize.height)
            }else{
                mTextureView.setAspectRatio(previewSize.height,previewSize.width)
            }
            configureTransform(width,height)
            mediaRecorder = MediaRecorder()
            initImageReader()
            manager.openCamera(cameraId, stateCallback, null)
        }catch (e:Exception){
            e.printStackTrace()
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
        }catch (e:Exception){
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
        activity?:return
        val rotation=(activity as FragmentActivity).windowManager.defaultDisplay.rotation
        var matrix=Matrix()
        val viewRect=RectF(0f,0f,viewWidth.toFloat(),viewHeight.toFloat())
        val bufferRect=RectF(0f,0f,previewSize.height.toFloat(),previewSize.width.toFloat())
        val centerX=viewRect.centerX()
        val centerY=viewRect.centerY()
        if(Surface.ROTATION_90==rotation||Surface.ROTATION_270==rotation){
            bufferRect.offset(centerX-bufferRect.centerX(),centerY-bufferRect.centerY())
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

    private fun initImageReader(){
        mImageReader =
            ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.YUV_420_888, 1)
        mImageReader?.setOnImageAvailableListener({
            val image = it.acquireNextImage()
            // Y:U:V == 4:2:2
            var y: ByteArray?=null
            var u: ByteArray?=null
            var v: ByteArray?=null
            val lock = ReentrantLock()
            if(image.format==ImageFormat.YUV_420_888){
                val planes: Array<Image.Plane> = image.planes
                // 加锁确保y、u、v来源于同一个Image
                lock.lock()
                if(y==null){
                    y = ByteArray(planes[0].buffer.limit() - planes[0].buffer.position())
                    u = ByteArray(planes[1].buffer.limit() - planes[1].buffer.position())
                    v = ByteArray(planes[2].buffer.limit() - planes[2].buffer.position())
                }
                if(image.planes[0].buffer.remaining()==y.size){
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
                }

                lock.unlock()
                image.close()
            }
        },backgroundHandler )
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


    /***
     * 回调的一些接口
     *
     * */

    //TextureView.SurfaceTextureListener
    private val surfaceTextureListener=object :TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) =Unit

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) =true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
           openCamera(width,height)
        }

    }

    //连接的相机设备 回调相机状态发生变化时
    //以用于接收相机状态的更新和后续的处理。
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            //当相机打开成功之后会回调此方法
            // 一般在此进行获取一个全局的CameraDevice实例，开启相机预览等操作
            cameraOpenCloseLock.release()
            this@FragmentCamera2NV21.cameraDevice = cameraDevice
            startPreview()
            configureTransform(mTextureView.width, mTextureView.height)
        }

        override fun onDisconnected(camera: CameraDevice) {
            //相机设备失去连接(不能继续使用)时回调此方法，同时当打开相机失败时也会调用此方法而不会调用onOpened()
            // 可在此关闭相机，清除CameraDevice引用
            cameraOpenCloseLock.release()
            cameraDevice?.close()
            this@FragmentCamera2NV21.cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            //相机发生错误时调用此方法
            cameraOpenCloseLock.release()
            cameraDevice?.close()
            this@FragmentCamera2NV21.cameraDevice = null
            activity?.finish()
        }

    }
}
