package wooyun.esnb.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import com.github.tools.interfaces.HandlePostBack
import com.github.tools.presenter.DataManager
import kotlinx.android.synthetic.main.activity_main.*
import wooyun.esnb.R
import wooyun.esnb.adapter.MainAdapter
import wooyun.esnb.base.BaseActivity
import wooyun.esnb.dialog.SelectPicPopupWindow
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.room.Note
import wooyun.esnb.room.NoteController
import wooyun.esnb.util.Tools
import wooyun.esnb.util.Tools.Companion.getScreenWidth
import wooyun.esnb.view.SpacesItemDecoration
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : BaseActivity() {
    private lateinit var about: MainAdapter
    private var menuWindow: SelectPicPopupWindow? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onStart() {
        super.onStart()
        isFirstIn()
        val all: List<Note>? = NoteController(this).init().getAll()
        val layoutManager = LinearLayoutManager(this)
        lv_note.layoutManager = layoutManager
        lv_note.addItemDecoration(SpacesItemDecoration(5))
        about = MainAdapter(this)
        about.setData(all)
        lv_note.adapter = about
        refresh_activity_main.setOnRefreshListener {
            com.github.tools.presenter.Tools.handlerPostDelayed(object : HandlePostBack {
                override fun doWork() {
                    about.setData(NoteController(this@MainActivity).init().getAll())
                    refresh_activity_main.isRefreshing = false
                }
            }, 500)
        }
    }


    override fun onResume() {
        super.onResume()
        btn_add.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                try {
                    //实例化SelectPicPopupWindow
                    menuWindow = SelectPicPopupWindow(this@MainActivity, itemsOnClick)
                    //显示窗口
                    menuWindow?.showAtLocation(this@MainActivity.findViewById(R.id.main), Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0) //设置layout在PopupWindow中显示的位置
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }


    private fun isFirstIn() {
        val pref = getSharedPreferences("isFirstIn", Context.MODE_PRIVATE)
        //取得相应的值，如果没有该值， 说明还未写入，用true作为默认值
        val isFirstIn = pref.getBoolean("isFirstIn", true)
        val editor = pref.edit()
        editor.putBoolean("isFirstIn", false)
        editor.apply()
        if (isFirstIn) {
            showFirstDialog()
        }
    }


    private fun showFirstDialog() {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.activity_md, null, false)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        val mWebView = view.findViewById<WebView>(R.id.markdownView)
        mWebView.loadUrl("file:///android_asset/updateLog.html")
        dialog.show()
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        Objects.requireNonNull(dialog.window).setLayout(getScreenWidth(this) / 4 * 3, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    private var firstTime = 0L

    override fun onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 500) {
            showSnackBar(title_bar, getString(R.string.click_exit))
            firstTime = System.currentTimeMillis()
        } else {
            val boolean = Tools().spGet(this, "wooyun.notepad_preferences").getBoolean("del_status", false)
            if (boolean) DataManager.deleteAllCache()
            finish()
        }
    }

    private fun showSnackBar(view: View, text: String) {
        var snackbar: Snackbar? = null
        if (snackbar == null) {
            snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        } else {
            snackbar.setText(text)
            snackbar.setDuration(Snackbar.LENGTH_SHORT)
        }
        snackbar.show()
    }


    //为弹出窗口实现监听类
    private val itemsOnClick = View.OnClickListener { v ->
        menuWindow?.dismiss()
        when (v.id) {
            R.id.btn_write -> {
                val intent = Intent()
                intent.setClass(this, EditActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
            R.id.btn_about -> {
                val intent1 = Intent()
                intent1.setClass(this, MActivity::class.java)
                startActivity(intent1)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
            R.id.btn_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }
    }
}