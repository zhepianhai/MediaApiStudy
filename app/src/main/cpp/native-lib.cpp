/**
 * 获取lame的版本号
 * */
 #include <jni.h>
#include "lame/lame.h"

extern "C"

JNIEXPORT jstring JNICALL
Java_com_zph_media_util_ZPHLameUtils_getLameVersion(JNIEnv *env, jclass thiz) {
    return env->NewStringUTF(get_lame_version());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_zph_media_util_ZPHLameUtils_convertmp3(JNIEnv *env, jclass clazz, jstring wav,
                                                jstring mp3) {

}