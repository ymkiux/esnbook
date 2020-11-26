package wooyun.esnb.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.PopupWindow
import android.widget.Toast
import com.github.tools.interfaces.HandlePostBack
import com.github.tools.presenter.DataManager
import kotlinx.android.synthetic.main.fragment_about.*
import wooyun.esnb.BuildConfig
import wooyun.esnb.R
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.sql.DbOpenHelper
import wooyun.esnb.util.Tools

class AboutFragment : Fragment() {
    private var popupWindow: PopupWindow? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_version.text = BuildConfig.VERSION_NAME
        //zf
        vw_donate1.setOnClickListener {
            if (Tools().is_Aible(requireActivity(), "com.eg.android.AlipayGphone")) {
                val intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
                        "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx09708xuo3gh6fsv2iid7%3F_s" +  //这里把｛URLcode｝替换成第一步扫描的结果
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
        //qq
        vw_qq.setOnClickListener {
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
        //update log
        vw_update_log.setOnClickListener(object : NoRepeatClickListener() {
            /**
             * 点击事件(相当于@link{android.view.View.OnClickListener})
             *
             * @param v 使用该限制点击的View
             */
            override fun onRepeatClick(v: View?) {
                showUpdatedLogView()
            }
        })
        //email
        vw_mail.setOnClickListener {
            Tools.feedback("mailto:3255284101@qq.com", requireActivity())
        }
        //clear data
        val myDb = DbOpenHelper(requireActivity())
        vm_Eliminate.setOnClickListener {
            val db = myDb.writableDatabase
            db.execSQL("delete from " + DbOpenHelper.TABLE)
            Toast.makeText(requireActivity(), getString(R.string.data_has_been_emptied), Toast.LENGTH_SHORT).show()
        }

        //get cache
        txtCacheSize.text = DataManager.getDefaultCacheSize()
        //clear cache
        vm_clear_cache.setOnClickListener {
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
            }).start()
        }
        //ky
        vm_ky.setOnClickListener {
            val intent = Intent(requireActivity(), KyActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out)
        }
    }

    //update log view
    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun showUpdatedLogView() {
        val displayMetrics = resources.displayMetrics
        @SuppressLint("InflateParams") val view = LayoutInflater.from(requireActivity()).inflate(R.layout.activity_md, null, false)
        val width = displayMetrics.widthPixels / 11 * 9
        val height = displayMetrics.heightPixels / 11 * 9
        popupWindow = PopupWindow(view, width, height)
        popupWindow!!.showAtLocation(view, Gravity.CENTER, 0, 0)
        val layoutParams = requireActivity().window.attributes
        layoutParams.alpha = 0.4f
        requireActivity().window.attributes = layoutParams
        popupWindow!!.setOnDismissListener {
            layoutParams.alpha = 1f
            requireActivity().window.attributes = layoutParams
        }
        popupWindow!!.animationStyle = R.style.UpdateDialog
        popupWindow!!.isFocusable = true
        val mWebView = view.findViewById<WebView>(R.id.markdownView)
        mWebView.loadUrl("file:///android_asset/updateLog.html")
        val webSettings = mWebView.settings
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        val dbPath = requireActivity().applicationContext.getDir("db", Context.MODE_PRIVATE).path
        webSettings.databasePath = dbPath
        webSettings.setAppCacheEnabled(true)
        val cachePath = requireActivity().applicationContext.getDir("db", Context.MODE_PRIVATE).path
        webSettings.databasePath = cachePath
        webSettings.setAppCacheMaxSize(5 * 1024 * 1024.toLong())
    }

    //handle
    private var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    txtCacheSize.text = msg.obj as String
                    Toast.makeText(requireActivity(), getString(R.string.cache_has_been_emptied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                testReturn()
                true
            } else false
        }
    }

    private fun testReturn() {
        if (popupWindow != null) {
            popupWindow!!.dismiss()
        }
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}