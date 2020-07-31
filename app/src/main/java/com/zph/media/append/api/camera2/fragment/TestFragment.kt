package com.zph.media.append.api.camera2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zph.media.R
import com.zph.media.append.api.camera2.base.Camera2SurfaceHelper
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * A simple [Fragment] subclass.
 */
class TestFragment : Fragment(), Camera2SurfaceHelper.Camera2HelpImp {

    companion object {
        fun newInstance(): TestFragment =
            TestFragment()
    }
    lateinit var camera2Helper: Camera2SurfaceHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            camera2Helper=Camera2SurfaceHelper(it,surfaceView)
            camera2Helper.setCamera2HelpImpl(this)
        }
        btn_take_pic.setOnClickListener {_->
            camera2Helper?.let {
                it.takePhoto()
            }
        }
        btn_record_view.setOnClickListener {
            camera2Helper?.let {
                it.takeVideo()
            }
        }

    }

    override fun onResume() {
        camera2Helper?.let {
            it.onResume()
        }
        super.onResume()
    }
    override fun onPause() {
        camera2Helper?.let {
            it.onPause()
        }
        super.onPause()
    }
    //拍照回调
    override fun camera2HelperImageImpl(path: String) {

    }
    //视频拍摄回调
    override fun camera2HelperVideoImpl(path: String) {

    }
}
