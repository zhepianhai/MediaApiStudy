package com.zph.media.append.opengles.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.View

abstract class Shape(mView: View) : GLSurfaceView.Renderer {
    protected var mView: View = mView
    fun loadShader(type: Int, shaderCode: String): Int {
        //根据type创建顶点着色器或者片元着色器
        var shader = GLES20.glCreateShader(type)
        //将资源加入到着色器，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

}