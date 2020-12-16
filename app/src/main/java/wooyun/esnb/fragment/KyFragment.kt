package wooyun.esnb.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import com.github.tools.interfaces.HandlerListener
import com.github.tools.task.HandlerTask
import kotlinx.android.synthetic.main.fragment_ky.*
import wooyun.esnb.R
import wooyun.esnb.bean.Bitmaps
import wooyun.esnb.controller.ImageDataController
import wooyun.esnb.controller.UrlController
import wooyun.esnb.cursom.LongPressToLimitEventsClickListener
import wooyun.esnb.interfaces.AppBarStateChangeListener
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.task.AdapterData


class KyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ky, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appearAnimation = AlphaAnimation(0f, 1f)
        appearAnimation.duration = 500
        iv_fragment_ky_image.startAnimation(appearAnimation)
        val bitmaps = Bitmaps.bitmaps
        if (bitmaps == null) {
            UrlController(requireActivity(), iv_fragment_ky_image)
        } else {
            val message = Message()
            message.what = 0
            message.obj = bitmaps
            HandlerTask(object : HandlerListener {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        0 -> {
                            iv_fragment_ky_image.setImageBitmap(msg.obj as Bitmap)
                        }
                    }
                }
            }).handler.sendMessage(message)
        }
        AdapterData(requireActivity()).initKyData(recycle_activity_ky_infos)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        app_bar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.EXPANDED) {
                    /*展开状态*/
                    toolbar.visibility = View.GONE
                } else if (state == State.COLLAPSED) {
                    /*折叠状态 中间状态*/
                    toolbar.visibility = View.VISIBLE
                }
            }
        })

        iv_fragment_ky_image.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                ImageDataController(requireActivity()).loadImagePage(iv_fragment_ky_image)
            }
        })

        iv_fragment_ky_image.setOnLongClickListener(object : LongPressToLimitEventsClickListener() {
            override fun onLongPressToLimitEventsClick(v: View?) {
                ImageDataController(requireActivity()).saveImage(iv_fragment_ky_image)
            }
        })

        float_fragment_ky_switch.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                UrlController(requireActivity(), iv_fragment_ky_image)
            }
        })
    }
}