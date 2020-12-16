package wooyun.esnb.fragment

import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tools.interfaces.HandlerListener
import com.github.tools.task.HandlerTask
import kotlinx.android.synthetic.main.fragment_picture.*
import wooyun.esnb.R
import wooyun.esnb.bean.Bitmaps
import wooyun.esnb.controller.ImageDataController
import wooyun.esnb.interfaces.DoubleClickLister
import wooyun.esnb.view.BlurBitmapUtil


class PictureFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picture, container, false)
    }

    override fun onResume() {
        super.onResume()
        relative_fragment_picture_bg.setOnClickListener(object : DoubleClickLister {
            override fun OnDoubleClickLister(v: View?) {
                ImageDataController(requireActivity()).saveImage(iv_fragment_picture_img)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bitmaps = Bitmaps.bitmaps
        val message = Message()
        message.what = 0
        message.obj = bitmaps
        HandlerTask(object : HandlerListener {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        val bitmap = msg.obj as android.graphics.Bitmap
                        val blurBitmap: android.graphics.Bitmap = BlurBitmapUtil.blurBitmap(iv_fragment_picture_bg.context, msg.obj as android.graphics.Bitmap, 20f)
                        iv_fragment_picture_bg.setImageBitmap(blurBitmap)
                        iv_fragment_picture_img.setImageBitmap(bitmap)
                    }
                }
            }
        }).handler.sendMessage(message)
    }
}