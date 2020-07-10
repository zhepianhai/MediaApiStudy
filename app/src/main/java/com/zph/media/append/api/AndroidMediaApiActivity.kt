package com.zph.media.append.api

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView.SurfaceTextureListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.util.BitmapUtils
import com.zph.media.util.CameraUtil
import com.zph.media.util.ToastUtil
import kotlinx.android.synthetic.main.activity_android_media_api.*
import java.util.*


/**
 * 视频，camera2 预览
 *
 * */

open class AndroidMediaApiActivity : BaseActivity() {

    private lateinit var camera: CameraDevice
    private lateinit var cameraManager: CameraManager
    private lateinit var childHandler: Handler
    private lateinit var mainHandler: Handler
    private var mCameraID = "0" //摄像头Id 0 为后  1为前
    private lateinit var mImageReader: ImageReader
    private lateinit var mCameraCaptureSession: CameraCaptureSession
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var mCameraSensorOrientation = 0        //摄像头方向
    private val REQUEST_CAMERA_CODE = 100
    private val ORIENTATIONS = SparseIntArray()
    private var handlerThread = HandlerThread("Camera2")
    private lateinit var videoSize: Size
    private lateinit var previewSize: Size
    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    companion object {
        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, AndroidMediaApiActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideoView()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_android_media_api
    }

    override fun initTopBar() {
//        tv_title.text = "MediaPlay"
//        lay_back.setOnClickListener {
//            finish()
//        }
    }

    private fun initVideoView() {
//        mSurfaceHolder = mSufaceView.surfaceTexture
//        mSurfaceHolder.set(true)
//        mSurfaceHolder.addCallback(SurfaceCallback())
//        mSurfaceHolder.setOnFrameAvailableListener {
//            initCamera2()
//        }
        mSufaceView.surfaceTextureListener = texturListener()

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        path="android.resource://"+ packageName +"/"+R.raw.test
        btn_take_pic.setOnClickListener {
            if (mCameraDevice != null && mSufaceView.isAvailable) {
                mCameraDevice?.apply {
                    val captureRequestBuilder =
                        createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                    captureRequestBuilder.addTarget(mImageReader?.surface)
                    val rotation = windowManager.defaultDisplay.rotation

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
                    mCameraCaptureSession?.capture(
                        captureRequestBuilder.build(),
                        null,
                        childHandler
                    )
                        ?: ToastUtil.showToast(this@AndroidMediaApiActivity, "拍照异常")
                }

            }

        }
    }

    private inner class texturListener : SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {

        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            Log.i("TAGG", "onSurfaceTextureAvailable")
            initCamera2(width, height)
        }

    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            // 释放Camera资源
            if (null != mCameraDevice) {
                mCameraDevice.close();
            }

        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            try {
//                initCamera2();

            } catch (e: Exception) {
            }
        }

    }

    private fun initCamera2(width: Int, height: Int) {

        handlerThread.start()
        childHandler = Handler(handlerThread.looper)
        mainHandler = Handler(mainLooper)
        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT


        //获取摄像头管理

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var strings: Array<String> = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(strings, REQUEST_CAMERA_CODE)


            return
        }
        try {
            for (cameraId in cameraManager.cameraIdList) {
                Log.i("TAGG", "cameraId:$cameraId")

                //描述相机设备的属性类
                val characteristics =
                    cameraManager.getCameraCharacteristics(cameraId)


                //获取是前置还是后置摄像头
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                //使用后置摄像头
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    val map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    if (map != null) {
                        var sizeMap = map.getOutputSizes(SurfaceTexture::class.java)
                        if (sizeMap == null) continue

                        //这个参数表明，获取的是对应surfaceTexture的输出分辨率，也就是对应textureView的分辨率
                        //找到不为空的，才能设置对应显示实时的大小
                        //从底层拿camera支持的previewsize，完了和屏幕分辨率做差，diff最小的就是最佳预览分辨率


                        videoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
                        previewSize = CameraUtil.chooseOptimalSize(
                            sizeMap, width, height,videoSize  )!!

//                        previewSize = Collections.max(
//                                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
//                         CompareSizesByArea());
//                        previewSize = CameraUtil.getOptimalSize(mSufaceView, characteristics)
                        mCameraID = cameraId
                        initImageReader()
                        //获取摄像头方向
                        mCameraSensorOrientation =
                            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                        Log.i("TAGG", "mCameraID:$mCameraID")
                        Log.i("TAGG", "previewSize:" + previewSize.height)
                        Log.i("TAGG", "previewSize:" + previewSize.width)
                        cameraManager.openCamera(mCameraID, stateCallback(), mainHandler)
                    }
                }

            }


        } catch (r: CameraAccessException) {
            Log.i("TAGG", "CameraAccessException:" + r.message)
        }


    }
    private fun chooseVideoSize(choices: Array<Size>) = choices.firstOrNull {
        it.width == it.height * 4 / 3 && it.width <= 1080 } ?: choices[choices.size - 1]

    private fun initImageReader() {
        mImageReader =
            ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
        mImageReader.setOnImageAvailableListener(ImageReader.OnImageAvailableListener {
            val image = it.acquireNextImage()
            val byteBuffer = image.planes[0].buffer
            val byteArray = ByteArray(byteBuffer.remaining())
            byteBuffer.get(byteArray)
            it.close()
            Log.i("TAGG", "mCameraSensorOrientation: $mCameraSensorOrientation")
            BitmapUtils.savePic(byteArray, true, { savedPath, time ->
                this.runOnUiThread {
                    ToastUtil.showToast(
                        this@AndroidMediaApiActivity,
                        "图片保存成功！ 保存路径：$savedPath 耗时：$time"
                    )
                }
            }, { msg ->
                this.runOnUiThread {
                    Log.i("TAGG", "错误: $msg")
                    ToastUtil.showToast(this@AndroidMediaApiActivity, "图片保存失败：$msg")
                }
            })
        }, childHandler)
    }

    private inner class stateCallback : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            //开启预览
            Log.i("TAGG", "StateCallback:")
            takePreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            if (null != mCameraDevice) {
                mCameraDevice.close()
            }
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Toast.makeText(this@AndroidMediaApiActivity, "开启摄像头失败！", Toast.LENGTH_LONG).show()
        }

    }

    //预览
    private fun takePreview() {
        try {
            previewRequestBuilder =
                mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            val surfaceTexture: SurfaceTexture = mSufaceView.surfaceTexture
            surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)
            val previewSurface = Surface(surfaceTexture)
            mSufaceView.setAspectRation(previewSize.width, previewSize.height)
            previewRequestBuilder.addTarget(previewSurface);
            var strings = listOf<Surface>(previewSurface, mImageReader.surface)
            mCameraDevice.createCaptureSession(strings, stateCallback1111(), childHandler)


        } catch (e: Exception) {
        }
    }

    private inner class stateCallback1111 : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.i("TAGG", "stateCallback1111:")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            if (null == mCameraDevice) return
            mCameraCaptureSession = session
            try {
                // 自动对焦
                previewRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                var previewRequest = previewRequestBuilder.build()
                mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler)
            } catch (e: Exception) {
            }
        }

    }

    override fun onDestroy() {
        releaseCamera()
        releaseThread()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(mCameraID, stateCallback(), childHandler)
            }
        }
    }


    /**
     * 释放资源
     * */
    fun releaseCamera() {
        mCameraCaptureSession?.close()

        mCameraDevice?.close()

        mImageReader?.close()
    }

    fun releaseThread() {
        handlerThread.quitSafely()
    }
}




