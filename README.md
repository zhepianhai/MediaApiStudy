# MediaApiStudy
《Android 音视频从入门到提高 —— 任务列表》

#1. 在 Android 平台绘制一张图片，使用至少 3 种不同的 API，ImageView，SurfaceView，自定义 View

#2. 在 Android 平台使用 AudioRecord 和 AudioTrack API 完成音频 PCM 数据的采集和播放，并实现读写音频 wav 文件

#3. 在 Android 平台使用 Camera API 进行视频的采集，分别使用 SurfaceView、TextureView 来预览 Camera 数据，取到 NV21 的数据回调

#4. 学习 Android 平台的 MediaExtractor 和 MediaMuxer API，知道如何解析和封装 mp4 文件

#5. 学习 Android 平台 OpenGL ES API，了解 OpenGL 开发的基本流程，使用 OpenGL 绘制一个三角形

#6. 学习 Android 平台 OpenGL ES API，学习纹理绘制，能够使用 OpenGL 显示一张图片

#7. 学习 MediaCodec API，完成音频 AAC 硬编、硬解

#8. 学习 MediaCodec API，完成视频 H.264 的硬编、硬解

#9. 串联整个音视频录制流程，完成音视频的采集、编码、封包成 mp4 输出

#10. 串联整个音视频播放流程，完成 mp4 的解析、音视频的解码、播放和渲染

#11. 进一步学习 OpenGL，了解如何实现视频的剪裁、旋转、水印、滤镜，并学习 OpenGL 高级特性，如：VBO，VAO，FBO 等等

#12. 学习 Android 图形图像架构，能够使用 GLSurfaceviw 绘制 Camera 预览画面

#13. 深入研究音视频相关的网络协议，如 rtmp，hls，以及封包格式，如：flv，mp4

#14. 深入学习一些音视频领域的开源项目，如 webrtc，ffmpeg，ijkplayer，librtmp 等等

#15. 将 ffmpeg 库移植到 Android 平台，结合上面积累的经验，编写一款简易的音视频播放器

#16. 将 x264 库移植到 Android 平台，结合上面积累的经验，完成视频数据 H264 软编功能

#17. 将 librtmp 库移植到 Android 平台，结合上面积累的经验，完成 Android RTMP 推流功能



=======================================================================================

框架部分

1.Lame
 LAME是目前最好的MP3编码引擎。LAME编码出来的MP3音色纯厚、空间宽广、低音清晰、细节表现良好，
 它独创的心理音响模型技术保证了CD音频还原的真实性，配合VBR和ABR参数，音质几乎可以媲美CD音频，
 但文件体积却非常小。

2. Ffmpeg:
   通过：CentOS Linux和 android-ndk-r20b-linux-x86_64和ffmpeg-4.2.2交叉编译后生成的静态库文件和动态库文件


下面是一些推荐的参考资料：

    1. 《雷霄骅的专栏》：http://blog.csdn.net/leixiaohua1020

    2. 《Android音频开发》：http://ticktick.blog.51cto.com/823160/d-15

    3. 《FFMPEG Tips》：http://ticktick.blog.51cto.com/823160/d-17

    4. 《Learn OpenGL 中文》：https://learnopengl-cn.readthedocs.io/zh/latest/

    5. 《Android Graphic 架构》：https://source.android.com/devices/graphics/


