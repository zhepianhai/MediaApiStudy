#include <jni.h>
#include <string>
extern  "C"
{
#include "include/libavcodec/avcodec.h"
#include "include/libavformat/avformat.h"
}



extern "C"
JNIEXPORT jstring JNICALL
Java_com_zph_media_append_gpuimage_GpuImageTestActivity_getFFmpegVersion(JNIEnv *env,
                                                                         jobject _) {
    std::string hello = "Hello from C++";
    avformat_version();
    return env->NewStringUTF(avcodec_configuration());
}


