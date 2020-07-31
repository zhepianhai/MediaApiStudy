package com.zph.media.append.opengles.render

import android.content.Context
import android.opengl.GLSurfaceView

class FGLView(context: Context?) : GLSurfaceView(context) {
    var renderer:FGLRender = FGLRender(this)
    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderer.shape = renderer
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setShape(clazz: Shape) = try {

    } catch (e: Exception) {
        e.printStackTrace()
    }

}