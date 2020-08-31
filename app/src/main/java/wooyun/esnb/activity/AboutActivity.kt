package wooyun.esnb.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.SQLException
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import wooyun.esnb.BuildConfig
import wooyun.esnb.R
import wooyun.esnb.sql.DbOpenHelper
import wooyun.esnb.cursom.NoRepeatClickListener
import wooyun.esnb.cursom.TitleBar
import wooyun.esnb.util.Utils
import java.io.IOException
import java.util.*

class AboutActivity : AppCompatActivity() {
    private var txtCacheSize: TextView? = null
    var myDb: DbOpenHelper? = null
    private var clearCache: TextView? = null
    private var tvUpdateLog: TextView? = null
    private var tvEliminate: TextView? = null
    private var tvQQ: TextView? = null
    private var tvDonate1: TextView? = null
    private var tv_Ky: TextView? = null
    private var tvMail: TextView? = null
    private var titleBar: TitleBar? = null

    private var popupWindow: PopupWindow? = null
    private var doc: Document? = null
    private var version_name: TextView? = null
    private var tv_if_version: TextView? = null
    private var update_f: Button? = null
    private var cloud_version1: String? = null
    private var local_version1: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(supportActionBar)!!.hide()
        setContentView(R.layout.activity_about)
        init()
        initView()
    }

    private fun initView() {
        val versionName = BuildConfig.VERSION_NAME
        try {
            version_name!!.text = versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            txtCacheSize!!.text = Utils.getTotalCacheSize(this)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        clearCache!!.setOnClickListener {
            val cachesize = txtCacheSize!!.text.toString()
            if ("0.0KB" == cachesize) {
                Toast.makeText(this@AboutActivity, R.string.Cache_cleared, Toast.LENGTH_SHORT).show()
            } else {
                Utils.clearAllCache(this)
                Thread.sleep(1000)
                val msg = Message()
                msg.what = 0
                handler.sendMessage(msg)
            }
        }
        myDb = DbOpenHelper(this)

        tvUpdateLog!!.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                showUpdated()
            }
        })
        tvEliminate!!.setOnClickListener({ showDialog() })


        tvQQ!!.setOnClickListener {
            if (is_Aible(this@AboutActivity, "com.tencent.mobileqq")) {
                try {
                    val qq = "3255284101"
                    val url = ("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + qq
                            + "&card_type=person&source=qrcode")
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this@AboutActivity, R.string.QQ_not_installed, Toast.LENGTH_SHORT).show()
            }
        }
        tvDonate1!!.setOnClickListener {
            if (is_Aible(this@AboutActivity, "com.eg.android.AlipayGphone")) {
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
                Toast.makeText(this@AboutActivity, R.string.Alipay_is_not_installed, Toast.LENGTH_SHORT).show()
            }
        }
        tv_Ky!!.setOnClickListener {
            val intent = Intent(this@AboutActivity, KyActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out)
        }

        tvMail!!.setOnClickListener { Utils.feedback("mailto:3255284101@qq.com", this) }
        if (MainActivity.hasKitKat() && !MainActivity.hasLollipop()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else if (MainActivity.hasLollipop()) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        titleBar!!.setTitle("")
        titleBar!!.setTitleColor(Color.WHITE)
        titleBar!!.setSubTitleColor(Color.WHITE)
        titleBar!!.setDividerColor(Color.GRAY)

        try {
            CheckUpdate()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val sharedPreferences1 = getSharedPreferences("version",
                Activity.MODE_PRIVATE)
        cloud_version1 = sharedPreferences1.getString("cloud_version", "")
        local_version1 = BuildConfig.VERSION_NAME
        version_name!!.setOnClickListener(object : NoRepeatClickListener() {
            @SuppressLint("SetTextI18n")
            override fun onRepeatClick(v: View?) {
                when {
                    local_version1 == cloud_version1 -> {
                        Toast.makeText(this@AboutActivity, R.string.been_updated, Toast.LENGTH_SHORT).show()
                    }
                    cloud_version1!! > local_version1!! -> {
                        show_update_Dialog()
                        tv_if_version!!.text = getString(R.string.update_to_new).toString() + cloud_version1 + getString(R.string.version)
                        tv_if_version!!.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                        update_f!!.setText(R.string.update_to_new)
                    }
                    else -> {
                        show_update_Dialog()
                        update_f!!.setText(R.string.Internal_test_version)
                    }
                }
            }
        })
    }

    private fun init() {
        version_name = findViewById(R.id.tv_version)
        txtCacheSize = findViewById(R.id.txtCacheSize)
        clearCache = findViewById(R.id.Clear_cache)
        tvUpdateLog = findViewById(R.id.tv_update_log)
        tvEliminate = findViewById(R.id.tv_Eliminate)
        tvQQ = findViewById(R.id.tv_qq)
        tvDonate1 = findViewById(R.id.tv_donate1)
        tv_Ky = findViewById(R.id.tv_kyna)
        tvMail = findViewById(R.id.tv_mail)
        titleBar = findViewById(R.id.title_bar)
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("version", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


    private fun cool_store() {
        if (is_Aible(this@AboutActivity, "com.coolapk.market")) {
            val appPkg = "wooyun.notepad"
            val marketPkg = "com.coolapk.market"
            try {
                if (TextUtils.isEmpty(appPkg)) return
                val uri = Uri.parse("market://details?id=$appPkg")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (!TextUtils.isEmpty(marketPkg)) {
                    intent.setPackage(marketPkg)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this@AboutActivity, getString(R.string.uninstalled_Kuan), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 检查更新
     */
    private fun CheckUpdate() {
        Thread(Runnable {
            try {
                doc = Jsoup.connect("https://www.coolapk.com/apk/zut.edu.cn.notepad").get()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val masthead = doc!!.select("span.list_app_info").first()
            val str = masthead.toString()
            val Cloud_version = str.substring(str.indexOf(">") + 1, str.lastIndexOf("<")).trim { it <= ' ' }
            val message = Message()
            message.obj = Cloud_version
            message.what = 1
            handler.sendMessage(message)
        }).start()
    }

    @SuppressLint("SetTextI18n")
    private fun show_update_Dialog() {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this).inflate(R.layout.activity_updatedailog, null, false)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        val tv_version = view.findViewById<TextView>(R.id.tv_version)
        update_f = view.findViewById(R.id.update_f)
        tv_if_version = view.findViewById(R.id.tv_when_version)
        tv_version.text = "当前版本：" + BuildConfig.VERSION_NAME
        update_f!!.setOnClickListener(View.OnClickListener {
            when {
                BuildConfig.VERSION_NAME == cloud_version1 -> {
                    Toast.makeText(this, R.string.been_updated, Toast.LENGTH_SHORT).show()
                }
                cloud_version1!! > local_version1!! -> {
                    cool_store()
                    dialog.dismiss()
                }
                else -> {
                    Toast.makeText(this, R.string.already_in_beta, Toast.LENGTH_SHORT).show()
                }
            }
        })
        dialog.show()
        Objects.requireNonNull(dialog.window).setLayout(Utils.getScreenWidth(this) / 4 * 3, LinearLayout.LayoutParams.WRAP_CONTENT)
    }


    private fun showDialog() {
        try {
            val db = myDb!!.writableDatabase
            db.execSQL("delete from " + DbOpenHelper.TABLE)
            Toast.makeText(this@AboutActivity, "数据已清空!", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this@AboutActivity, R.string.wrong, Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("HandlerLeak")
    val handler: Handler = object : Handler() {
        @SuppressLint("NewApi", "SetTextI18n")
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                Toast.makeText(this@AboutActivity, R.string.Clean_up_completed, Toast.LENGTH_SHORT).show()
                txtCacheSize!!.text = Utils.getTotalCacheSize(this@AboutActivity)
            }

            if (msg.what == 1) {
                Thread(Runnable {
                    val inter_version = msg.obj as String
                    val sharedPreferences = getSharedPreferences("version", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("cloud_version", inter_version)
                    editor.apply()
                }).run()
            }
        }
    }


    /**
     * 重写onBackPressed方法监听物理返回键
     */
    override fun onBackPressed() {
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
            popupWindow = null
        } else {
            val intent = Intent(this@AboutActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    /**
     * popupWindow自定义布局
     */
    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun showUpdated() {
        val displayMetrics = resources.displayMetrics
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this).inflate(R.layout.activity_md, null, false)
        val width = displayMetrics.widthPixels / 11 * 9
        val height = displayMetrics.heightPixels / 11 * 9
        popupWindow = PopupWindow(view, width, height)
        popupWindow!!.showAtLocation(view, Gravity.CENTER, 0, 0)
        val layoutParams = window.attributes
        layoutParams.alpha = 0.4f
        window.attributes = layoutParams
        popupWindow!!.setOnDismissListener {
            layoutParams.alpha = 1f
            window.attributes = layoutParams
        }
        popupWindow!!.animationStyle = R.style.UpdateDialog
        popupWindow!!.isFocusable = true
        val mWebView = view.findViewById<WebView>(R.id.markdownView)
        mWebView.loadUrl("file:///android_asset/updateLog.html")
        val webSettings = mWebView.settings
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        val dbPath = applicationContext.getDir("db", Context.MODE_PRIVATE).path
        webSettings.databasePath = dbPath
        webSettings.setAppCacheEnabled(true)
        val cachePath = applicationContext.getDir("db", Context.MODE_PRIVATE).path
        webSettings.databasePath = cachePath
        webSettings.setAppCacheMaxSize(5 * 1024 * 1024.toLong())
    }

    /**
     * @param context     当前安装程序信息
     * @param packageName 当前安装程序包名信息
     * @return
     */
    fun is_Aible(context: Context, packageName: String?): Boolean {
        val packageManager = context.packageManager
        val packageInfos = packageManager.getInstalledPackages(0)
        val packageNames: MutableList<String> = ArrayList()
        if (packageInfos != null) {
            for (i in packageInfos.indices) {
                val packName = packageInfos[i].packageName
                packageNames.add(packName)
            }
        }
        return packageNames.contains(packageName)
    }
}