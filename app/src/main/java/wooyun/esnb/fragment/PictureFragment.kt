package wooyun.esnb.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tools.operating.L
import com.github.tools.task.ToolsTask
import kotlinx.android.synthetic.main.fragment_picture.*
import wooyun.esnb.R
import wooyun.esnb.bean.Bitmaps
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.view.BlurBitmapUtil


class PictureFragment : Fragment() {

    private var bitmap: android.graphics.Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false)
    }

    override fun onStart() {
        super.onStart()
        val bitmaps = Bitmaps.bitmaps
        val message = Message()
        message.obj = bitmaps
        handler.sendMessage(message)
    }


    override fun onResume() {
        super.onResume()
        btn_fragment_picture_down.setOnClickListener(object : NoRepeatClickListener() {
            /**
             * 点击事件(相当于@link{android.view.View.OnClickListener}
             * @param v 使用该限制点击的View
             */
            override fun onRepeatClick(v: View?) {
                if (bitmap != null) {
                    val saveImg = ToolsTask.saveImg(bitmap!!)
                    if (saveImg) {
                        L.t(getString(R.string.saved_successfully))
                        return
                    }
                    L.t(getString(R.string.save_failed))
                }
            }
        })
    }

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            bitmap = msg.obj as android.graphics.Bitmap
            val blurBitmap: android.graphics.Bitmap = BlurBitmapUtil.blurBitmap(iv_fragment_picture_bg.context, msg.obj as android.graphics.Bitmap, 20f)
            iv_fragment_picture_bg.setImageBitmap(blurBitmap)
            iv_fragment_picture_img.setImageBitmap(msg.obj as android.graphics.Bitmap)
            btn_fragment_picture_down.background = BitmapDrawable(blurBitmap)
        }
    }
}