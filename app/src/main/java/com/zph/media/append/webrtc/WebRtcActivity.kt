package com.zph.media.append.webrtc

import android.app.Activity
import android.content.Intent
import com.zph.media.R
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web_rtc.*
import kotlinx.android.synthetic.main.layout_navi.*
import org.webrtc.*
import org.webrtc.PeerConnectionFactory.InitializationOptions


class WebRtcActivity : BaseActivity() {

    companion object {
        fun openActivity(activity: Activity) {
            val intent = Intent(activity, WebRtcActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_web_rtc
    }

    override fun initTopBar() {
        tv_title.text = "WebRtc学习"
        lay_back.setOnClickListener {
            finish()
        }
        initView()
    }
    private fun initView(){
        initWebRtc()
    }
    private fun initWebRtc(){
        // create PeerConnectionFactory

        // create PeerConnectionFactory
        val initializationOptions =
            InitializationOptions.builder(this).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        val peerConnectionFactory =
            PeerConnectionFactory.builder().createPeerConnectionFactory()

        // create AudioSource

        // create AudioSource
        val audioSource: AudioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        val audioTrack: AudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        val eglBaseContext = EglBase.create().eglBaseContext

        val surfaceTextureHelper =
            SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        // create VideoCapturer
        // create VideoCapturer
        val videoCapturer: VideoCapturer = createCameraCapturer()!!
        val videoSource: VideoSource =
            peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource.capturerObserver
        )
        videoCapturer.startCapture(480, 640, 30)
        localView.setMirror(true)
        localView.init(eglBaseContext, null)

        // create VideoTrack

        // create VideoTrack
        val videoTrack = peerConnectionFactory.createVideoTrack("101", videoSource)
        // display in localView
        // display in localView
        videoTrack.addSink(localView)
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator = Camera1Enumerator( )
        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

}