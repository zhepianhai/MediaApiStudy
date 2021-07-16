/**
 * 获取lame的版本号
 * */
 #include <jni.h>
#include <string.h>
#include <malloc.h>
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

}extern "C"
JNIEXPORT jstring JNICALL
Java_com_zph_media_util_NDKTestUtils_00024Companion_operateString(JNIEnv *env, jobject thiz,
                                                                  jstring s) {
   const char*strFromJava=env->GetStringUTFChars(s,NULL);
   if(strFromJava==NULL){
       return NULL;
   }
   char buff[128]={0};
   strcpy(buff,strFromJava);
   strcat(buff,"+ndk");
   //ava字符串转C/C++字符串: 使用GetStringUTFChars函数,必须调用ReleaseStringUTFChars释放内存。
   env->ReleaseStringUTFChars(s,strFromJava);
    return env->NewStringUTF(buff);

}

//求和的
extern "C"
JNIEXPORT jint JNICALL
Java_com_zph_media_util_NDKTestUtils_00024Companion_sumArray(JNIEnv *env, jobject thiz,
                                                             jintArray array) {
    int result=0;
    jint arr_len=env->GetArrayLength(array);
    //动态申请数组
    jint  *c_array= static_cast<jint *>(malloc(arr_len * sizeof(jint)));
    //初始化所有为0
    memset(c_array,0, sizeof(jint)*arr_len);
    //将java数组的[0-arr_len)位置的元素拷贝到c_array数组中
    env->GetIntArrayRegion(array,0,arr_len,c_array);
    for(int i=0;i<arr_len;++i){
        result+=c_array[i];
    }
    //释放
    free(c_array);
    return result;
}
//对象数组
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_zph_media_util_NDKTestUtils_00024Companion_init2DArray(JNIEnv *env, jobject thiz,
                                                                jint size) {

}