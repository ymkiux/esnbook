package wooyun.esnb.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import wooyun.esnb.R
import wooyun.esnb.fragment.SettingFragment
import wooyun.esnb.view.TitleBar
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, SettingFragment(), null)
                .commit()
    }


    @SuppressLint("CommitPrefEdits")
    override fun onBackPressed() {
        super.onBackPressed()
//        val sharedPreferences = Tools().spGet(this, "wooyun.notepad_preferences")
//        if (!Str.isUrl(sharedPreferences.getString("edit_pt", ""))) {
//            sharedPreferences.edit().putString("edit_pt", "")
//        }
        Intent_Activity()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}
