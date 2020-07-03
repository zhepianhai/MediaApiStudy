package com.zph.media.append.api.camera2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zph.media.R
import com.zph.media.base.BaseActivity

/**
 *
 * 初始化流程:
初始化动态授权,这是基本操作
初始化一个子线程的Handler,Camera2的操作可以放在主线程也可以放在子线程.按例一般都是子线程里,但是Camera2只需要我们提供一个子线程的Handler就行了.
初始化ImageReader,这个没有初始化顺序要求,并且它有数据回调接口,接口回调的图片数据我们直接保存到内部存储空间,所以提前初始化提供给后续使用.
初始化TextureView,添加TextureView的接口回调.
在TextureView的接口回调里回调启用成功方法后,我们开始初始化相机管理类initCameraManager
然后继续初始化CameraDevice.StateCallback 摄像头设备状态接口回调类,先初始化提供给后续使用.(在这个接口类的开启相机的回调方法里,我们需要实现创建预览图像请求配置和创建获取数据会话)
继续初始化CameraCaptureSession.StateCallback 摄像头获取数据会话类的状态接口回调类,先初始化提供给后续使用.(在这个接口类的配置成功回调方法里,我们需要实现预览图像或者实现拍照)
继续初始化CameraCaptureSession.CaptureCallback 摄像头获取数据会话类的获取接口回调类,先初始化提供给后续使用.(啥都不干)
判断摄像头前后,选择对应id
打开指定id的摄像头
实现拍照
 */


class Camera2Test1Activity : BaseActivity() {


    companion object {

        open fun openActivity(activity: Activity) {
            val intent = Intent(activity, Camera2Test1Activity::class.java)
            activity.startActivity(intent)
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_camera2_test1
    }

    override fun initTopBar() {

    }


}
