package wooyun.esnb.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tools.interfaces.HandlePostBack
import com.github.tools.operating.L
import com.github.tools.task.ToolsTask
import kotlinx.android.synthetic.main.fragment_ky.*
import wooyun.esnb.R
import wooyun.esnb.activity.MActivity
import wooyun.esnb.adapter.KyAdapter
import wooyun.esnb.bean.Bitmaps
import wooyun.esnb.bean.FiguresAndSets
import wooyun.esnb.bean.Ky
import wooyun.esnb.cursom.LongPressToLimitEventsClickListener
import wooyun.esnb.interfaces.AppBarStateChangeListener
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.task.BitmapTaskLoader
import wooyun.esnb.util.Tools


class KyFragment : Fragment(), LoaderManager.LoaderCallbacks<FiguresAndSets> {

    private var imageUrl: String? = null
    private var list: MutableList<Ky>? = ArrayList<Ky>()
    private var bitmaps: Bitmap? = null
    private val ID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportLoaderManager.initLoader(ID, null, this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ky, container, false)
    }

    private val handler: Handler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    val mutableList = msg.obj as FiguresAndSets
                    imageUrl = mutableList.api
                    bitmaps = mutableList.bitmap
                    if (bitmaps != null) {
                        loadImg(bitmaps!!)
                        return
                    }
                    loadImg()
                }
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val string = Tools().spGet(requireActivity(), "wooyun.notepad_preferences").getString("edit_pt", "")
        if (string != null && string.isNotEmpty()) imageUrl = string
        list!!.add(Ky("tools", "tools is an open source library by me, adapted to Android", "https://github.com/ymkiux/tools"))
        val layoutManager = LinearLayoutManager(requireActivity())
        recycle_activity_ky_infos.layoutManager = layoutManager
        val kyAdapter = KyAdapter(requireActivity())
        kyAdapter.setData(list)
        recycle_activity_ky_infos.adapter = kyAdapter

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
                bitmaps = (iv_fragment_ky_image.drawable as BitmapDrawable).bitmap
                when (bitmaps) {
                    null -> L.t(requireActivity().getString(R.string.wrong))
                    else -> {
                        Bitmaps.bitmaps = bitmaps
                        val intent = Intent(requireActivity(), MActivity::class.java)
                        intent.putExtra("Fragment", 1)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        com.github.tools.presenter.Tools.handlerPostDelayed(object : HandlePostBack {
                            /** callback interface allows delayed operations to be implemented **/
                            override fun doWork() {
                                requireActivity().finish()
                            }
                        }, 500)
                    }
                }
            }
        })

        iv_fragment_ky_image.setOnLongClickListener(object : LongPressToLimitEventsClickListener() {
            override fun onLongPressToLimitEventsClick(v: View?) {
                val saveImg = ToolsTask.saveImg(bitmaps!!)
                if (saveImg) {
                    L.t(getString(R.string.saved_successfully))
                    // this.setStatus(false)
                    return
                }
                L.t(getString(R.string.save_failed))
            }
        })


        float_fragment_ky_switch.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                loadImg()
            }
        })
    }

    private fun loadImg(bitmap: Bitmap) {
        iv_fragment_ky_image.setImageBitmap(bitmap)
    }


    private fun loadImg() {
        val bitmap = ToolsTask.getBitmap(imageUrl!!)
        iv_fragment_ky_image.setImageBitmap(bitmap)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<FiguresAndSets> {
        return BitmapTaskLoader(requireActivity())
    }

    override fun onLoadFinished(p0: Loader<FiguresAndSets>, p1: FiguresAndSets?) {
        val message = Message()
        message.what = 0
        message.obj = p1
        handler.sendMessage(message)
        //执行完相应操作之后便注销掉这个活动 避免二次加载当前方法
        requireActivity().supportLoaderManager.destroyLoader(ID)
    }

    override fun onLoaderReset(p0: Loader<FiguresAndSets>) {

    }

}