#include <jni.h>
#include <string>

#include "lame.h"
//获取版本号
extern "C"
JNIEXPORT jstring JNICALL
Java_com_zph_media_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(get_lame_version());
}