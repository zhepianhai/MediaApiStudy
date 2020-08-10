package com.zph.media.util;

public class ZPHLameUtils {
    /**
     * wav转换成mp3的本地方法
     *
     * @param wav
     * @param mp3
     */
    public static native void convertmp3(String wav, String mp3);

    /**
     * 获取LAME的版本信息
     *
     * @return
     */
    public static native String getLameVersion();
}
