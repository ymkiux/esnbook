package zut.edu.cn.notepad

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import zut.edu.cn.notepad.activity.MainActivity
import zut.edu.cn.notepad.cursom.OvalImageView


class WelcomeActivity : AppCompatActivity() {
    var textView: TextView? = null
    private var flag: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_welcome)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        val layout3 = findViewById<LinearLayout>(R.id.Layout3)
        textView = findViewById(R.id.TextView1)
        initView()
        val a = AnimationUtils.loadAnimation(this@WelcomeActivity, R.anim.jianbian)
        layout3.startAnimation(a)
        clearImageDiskcache(this)
    }


    private fun clearImageDiskcache(context: Context) {
        try {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                Thread(Runnable { Glide.get(context).clearDiskCache() }).start()
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
        var packageName: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            flag = PackageManager.MATCH_UNINSTALLED_PACKAGES;
        }
        val installedPackages = flag?.let { packageManager.getInstalledPackages(it) }
        for (installedPackage in installedPackages!!) {
            packageName = installedPackage.packageName as String?
        }
        val timer: CountDownTimer = object : CountDownTimer(3000, 10) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val min = millisUntilFinished.div(1000).toString();
                try {
                    textView!!.text = min + "秒"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
        timer.start()
        val actionBar = supportActionBar
        actionBar?.hide()
        handler.sendEmptyMessageDelayed(0, 2000)
        try {
            val mtl1 = findViewById<OvalImageView>(R.id.iv1)
            Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=3255284101&s=640").into(mtl1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

}