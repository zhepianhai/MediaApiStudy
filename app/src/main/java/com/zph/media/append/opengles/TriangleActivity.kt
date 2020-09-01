package com.zph.media.append.opengles

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zph.media.R
import com.zph.media.append.opengles.drawer.IDrawer
import com.zph.media.append.opengles.drawer.TriangleDrawer
import com.zph.media.append.opengles.render.SimpleRender
import com.zph.media.base.BaseActivity
import kotlinx.android.synthetic.main.activity_triangle.*
import kotlinx.android.synthetic.main.layout_navi.*

/**
 * OpenglEs 绘制三角形
 *
 * */
class TriangleActivity : BaseActivity() {
    private lateinit var drawer: IDrawer

    companion object {
        fun openActivity(activity: Activity) {
            val intent = Intent(activity, TriangleActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_triangle
    }

    override fun initTopBar() {
        tv_title.text = "OpenGL三角形"
        lay_back.setOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        drawer=TriangleDrawer()
        initRender(drawer)
    }
    private fun initRender(drawer: IDrawer) {
        gl_surface.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        gl_surface.setRenderer(render)
    }
    override fun onDestroy() {
        drawer.release()
        super.onDestroy()
    }


}
