package wooyun.esnb.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.Window
import wooyun.esnb.R
import wooyun.esnb.cursom.TitleBar
import java.util.*

class SettingActivity : AppCompatActivity() {
    private var titleBar: TitleBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(supportActionBar)!!.hide()
        setContentView(R.layout.activity_setting)
        init()
        initView()
    }

    private fun Intent_Activity() {
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun initView() {
        titleBar!!.setLeftImageResource(R.drawable.ic_arrow_back_black_24dp)
        titleBar!!.setLeftClickListener(View.OnClickListener { v: View? -> Intent_Activity() })
    }

    private fun init() {
        titleBar = findViewById(R.id.title_bar)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent_Activity()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
