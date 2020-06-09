package com.zph.media.append.api

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.SparseIntArray
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.activity_android_media_api.*
import kotlinx.android.synthetic.main.layout_navi.*
/**
 * 视频，camera2 预览
 *
 * */

open class AndroidMediaApiActivity : BaseActivity() {

    private lateinit var camera: CameraDevice
    private lateinit var cameraManager: CameraManager
    private lateinit var mSurfaceHolder: SurfaceHolder
    private lateinit var childHandler: Handler
    private lateinit var mainHandler: Handler
    private var mCameraID = "0" //摄像头Id 0 为后  1 为前
    private lateinit var mImageReader: ImageReader
    private lateinit var mCameraCaptureSession: CameraCaptureSession
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var previewRequestBuilder: CaptureRequest.Builder

    private val REQUEST_CAMERA_CODE = 100
    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
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
        tv_title.text = "MediaPlay"
        lay_back.setOnClickListener {
            finish()
        }
    }

    private fun initVideoView() {
        mSurfaceHolder = mSufaceView.holder
        mSurfaceHolder.setKeepScreenOn(true)
        mSurfaceHolder.addCallback(SurfaceCallback())


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
//        path="android.resource://"+ packageName +"/"+R.raw.test

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
                initCamera2();

            } catch (e: Exception) {
            }
        }

    }

    private fun initCamera2() {
        var handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper)
        mainHandler = Handler(mainLooper)
        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.YUV_420_888, 1)
        mImageReader.setOnImageAvailableListener(ImageReader.OnImageAvailableListener {
            var image = it.acquireLatestImage()
            var buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (null != bitmap) {
                img_picpreview.visibility = View.VISIBLE
                img_picpreview.setImageBitmap(bitmap)
            }

        }, mainHandler)

        //获取摄像头管理

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var strings: Array<String> = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(strings, REQUEST_CAMERA_CODE);


            return
        }
        cameraManager.openCamera(mCameraID, stateCallback(), mainHandler)
    }

    private inner class stateCallback : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            //开启预览
            takePreview();
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
            previewRequestBuilder.addTarget(mSurfaceHolder.surface);
            var strings = listOf<Surface>(mSurfaceHolder.surface, mImageReader.surface)
            mCameraDevice.createCaptureSession(strings, stateCallback1111(), childHandler)


        } catch (e: Exception) {
        }
    }

    private inner class stateCallback1111 : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {

        }

        override fun onConfigured(session: CameraCaptureSession) {
            if (null == mCameraDevice) return
            mCameraCaptureSession = session
            try {
                // 自动对焦
                previewRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                );
                var previewRequest = previewRequestBuilder.build()
                mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler)
            } catch (e: Exception) {
            }
        }

    }

    override fun onDestroy() {

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
                cameraManager.openCamera(mCameraID, stateCallback(), mainHandler)
            }
        }
    }
}




