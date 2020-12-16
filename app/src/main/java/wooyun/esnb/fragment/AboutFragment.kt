package wooyun.esnb.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.github.tools.interfaces.HandlePostBack
import com.github.tools.presenter.DataManager
import kotlinx.android.synthetic.main.fragment_about.*
import wooyun.esnb.BuildConfig
import wooyun.esnb.R
import wooyun.esnb.activity.MActivity
import wooyun.esnb.activity.MainActivity
import wooyun.esnb.adapter.AboutAdapter
import wooyun.esnb.bean.About
import wooyun.esnb.controller.GetBitmapController
import wooyun.esnb.interfaces.MissionCompletedCall
import wooyun.esnb.interfaces.OnCallBack
import wooyun.esnb.interfaces.SupplementCallBack
import wooyun.esnb.interfaces.onBackPressed
import wooyun.esnb.sql.DbOpenHelper
import wooyun.esnb.util.Tools
import wooyun.esnb.view.CustomPopupWindow
import java.util.*

class AboutFragment : Fragment(), onBackPressed {
    private var popupWindow: PopupWindow? = null
    private var ArrayList: MutableList<About> = ArrayList()
    private var missionCompletedCall: MissionCompletedCall? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        GetBitmapController(requireActivity())
        return inflater.inflate(R.layout.fragment_about, container, false)
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is MActivity) {
            activity.setBackListener(this)
            activity.setInterception(true);
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ArrayList.add(About(BuildConfig.VERSION_NAME, R.drawable.ic_version))
        ArrayList.add(About(getString(R.string.donation), R.drawable.ic_love))
        ArrayList.add(About(getString(R.string.qq_group), R.drawable.ic_qq_group))
        ArrayList.add(About(getString(R.string.update_log), R.drawable.ic_list_black_24dp))
        ArrayList.add(About(getString(R.string.send_mail), R.drawable.ic_mail))
        ArrayList.add(About(getString(R.string.Clear_data), R.drawable.ic_clear_black_24dp))
        ArrayList.add(About(getString(R.string.Eliminate), R.drawable.ic_clear_cache))
        ArrayList.add(About(getString(R.string.ky), R.drawable.ic_open_source))
        val layoutManager = LinearLayoutManager(requireActivity())
        recycle_fragment_about_info.layoutManager = layoutManager
        val about = AboutAdapter(requireActivity())
        about.setData(ArrayList)
        recycle_fragment_about_info.adapter = about

        about.setSupplementCall(object : SupplementCallBack {
            override fun onCall(position: Int, textView: TextView) {
                when (position) {
                    6 -> {
                        textView.text = DataManager.getDefaultCacheSize()
                    }
                }
            }
        })


        about.setOnCall(object : OnCallBack {
            override fun onClick(position: Int, textView: TextView) {
                when (position) {
                    0 -> {
                    }
                    1 -> pay()
                    2 -> qq()
                    3 -> {
                        //解决无焦点问题
                        view!!.post {
                            updatedLogView()
                        }
                    }
                    4 -> Tools.feedback("mailto:3255284101@qq.com", requireActivity())
                    5 -> {
                        val myDb = DbOpenHelper(requireActivity())
                        val db = myDb.writableDatabase
                        db.execSQL("delete from " + DbOpenHelper.TABLE)
                        Toast.makeText(requireActivity(), getString(R.string.data_has_been_emptied), Toast.LENGTH_SHORT).show()
                    }
                    6 -> {
                        Thread(Runnable {
                            DataManager.deleteAllCache()
                            com.github.tools.presenter.Tools.handlerPostDelayed(object : HandlePostBack {
                                /** callback interface allows delayed operations to be implemented **/
                                override fun doWork() {
                                    val defaultCacheSize = DataManager.getDefaultCacheSize()
                                    val message = Message()
                                    message.what = 0
                                    message.obj = defaultCacheSize
                                    handler.sendMessage(message)
                                }
                            }, 1000, true)
                            missionCompletedCall?.onCall(textView)
                        }).start()
                    }
                    7 -> {
                        val intent = Intent(requireActivity(), MActivity::class.java)
                        intent.putExtra("Fragment", 2)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out)
                        requireActivity().finish()
                    }
                }
            }
        })
    }

    //update log view
    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun updatedLogView() {
        val lp: WindowManager.LayoutParams = requireActivity().getWindow().getAttributes()
        lp.alpha = 0.3f
        requireActivity().getWindow().setAttributes(lp)
        val customPopupWindow = CustomPopupWindow(requireActivity())
        customPopupWindow.showAtLocation(customPopupWindow.mPopView,
                Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
    }


    //handle
    private var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    setCall(object : MissionCompletedCall {
                        override fun onCall(textView: TextView) {
                            textView.text = DataManager.getDefaultCacheSize()
                        }
                    })
                    Toast.makeText(requireActivity(), getString(R.string.the_purge_was_successful), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setCall(missionCompletedCall: MissionCompletedCall) {
        this.missionCompletedCall = missionCompletedCall
    }


    private fun testReturn() {
        if (popupWindow != null) {
            popupWindow!!.dismiss()
            return
        }
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackForward() {
        testReturn()
    }


    private fun pay() {
        if (Tools().is_Aible(requireActivity(), "com.eg.android.AlipayGphone")) {
            val intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
                    "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx09708xuo3gh6fsv2iid7%3F_s" +
                    "%3Dweb-other&_t=1472443966571#Intent;" +
                    "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
            try {
                val intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME)
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireActivity(), R.string.Alipay_is_not_installed, Toast.LENGTH_SHORT).show()
        }
    }


    private fun qq() {
        if (Tools().is_Aible(requireActivity(), "com.tencent.mobileqq")) {
            try {
                val qq = "3255284101"
                val url = ("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + qq
                        + "&card_type=person&source=qrcode")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireActivity(), R.string.QQ_not_installed, Toast.LENGTH_SHORT).show()
        }
    }


}