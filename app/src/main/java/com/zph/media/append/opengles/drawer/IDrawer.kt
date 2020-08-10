package com.zph.media.append.opengles.drawer

import android.graphics.SurfaceTexture

interface IDrawer {
    fun setVideoSize(videoW: Int, videoH: Int)
    fun setWorldSize(worldW: Int, worldH: Int)
    fun setAlpha(alpha: Float)
    fun draw()
    fun setTextureID(id: Int)
    fun getSurfaceTexture(cb: (st: SurfaceTexture)->Unit) {}
    fun release()
}