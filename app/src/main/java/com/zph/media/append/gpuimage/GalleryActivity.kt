package com.zph.media.append.gpuimage

import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.zph.media.R
import com.zph.media.base.BaseActivity
import com.zph.media.config.Constants
import com.zph.media.append.gpuimage.utils.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools
import kotlinx.android.synthetic.main.layout_navi.*
import java.io.File

class GalleryActivity : BaseActivity() {

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }

    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    private val gpuImageView: GPUImageView by lazy { findViewById(R.id.gpuimage) }
    private val seekBar: SeekBar by lazy { findViewById(R.id.seekBar) }



    override fun getLayoutId(): Int {
        return R.layout.activity_gallery
    }

    override fun initTopBar() {
        tv_title.text = "MediaPlay"
        lay_back.setOnClickListener {
            finish()
        }
        initView()
    }
    private fun initView(){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filterAdjuster?.adjust(progress)
                gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<View>(R.id.button_choose_filter).setOnClickListener {
            GPUImageFilterTools.showDialog(this) { filter ->
                switchFilterTo(filter)
                gpuImageView.requestRender()
            }
        }
        findViewById<View>(R.id.button_save).setOnClickListener { saveImage() }

        startPhotoPicker()

    }
    private fun switchFilterTo(filter: GPUImageFilter) {
        if (gpuImageView.filter == null || gpuImageView.filter.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
            filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
            if (filterAdjuster!!.canAdjust()) {
                seekBar.visibility = View.VISIBLE
                filterAdjuster!!.adjust(seekBar.progress)
            } else {
                seekBar.visibility = View.GONE
            }
        }
    }
    private fun startPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE)
    }

    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        var filePath=
            Environment.getExternalStorageDirectory().path + File.separator + Constants.APP_HOME_PATH_ + Constants.ZPH_GPUIMAGE_PATH
        gpuImageView.saveToPictures(filePath, fileName) { uri ->
            Toast.makeText(this, "Saved: $uri", Toast.LENGTH_SHORT).show()
        }
    }
}
