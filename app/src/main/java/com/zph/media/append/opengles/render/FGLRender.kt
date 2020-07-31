package com.zph.media.append.opengles.render

import android.opengl.GLES20
import android.view.View
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FGLRender(mView: View) : Shape(mView) {
    lateinit var shape: Shape
    private var triangleCoords = floatArrayOf(
        0.5f, 0.5f, 0.0f,  // top
        -0.5f, -0.5f, 0.0f,  // bottom left
        0.5f, -0.5f, 0.0f // bottom right
    )
    //颜色数据
    var color: FloatArray? = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) //白色

    var vertexShaderCode = "attribute vec4 vPosition;\n" +
            "void main() {\n" +
            "    gl_Position = vPosition;\n" +
            "}"
    var fragmentShaderCode = "precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "void main() {\n" +
            "    gl_FragColor = vColor;\n" +
            "}"

    private lateinit var vertexBuffer: FloatBuffer
    private var mProgram:Int = 0
    private var mPositionHandle = 0
    val COORDS_PER_VERTEX = 3
    //顶点个数
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX

    //顶点之间的偏移量
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 每个顶点四个字节


    //第五步 最后在onDrawFrame中绘制
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        /将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer)

        //获取片元着色器的vColor成员的句柄
        var mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    //第四步 在onSurfaceChanged中设置设置视图窗口：
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
    }
    //第三步 在onSurfaceCreated方法中，我们来创建program对象，连接顶点和片元着色器，链接program对象。
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //将背景设置为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //申请底层空间
        var bb: ByteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        //将坐标数据转化为FloatBuffer,用以传入给OpenGL es程序
         vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
        var vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        var fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        //创建一个空的OpenGLES程序
         mProgram=GLES20.glCreateProgram()
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader)
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader)
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram)
    }
}