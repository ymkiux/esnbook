package wooyun.esnb.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_about.*
import wooyun.esnb.R
import java.util.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(supportActionBar)!!.hide()
        setContentView(R.layout.activity_about)
        initView()
        initData()
    }

    private fun initData() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.frag_module, AboutFragment(), null)
                .commit()
    }


    private fun initView() {
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
        title_bar!!.setTitle("")
        title_bar!!.setTitleColor(Color.WHITE)
        title_bar!!.setSubTitleColor(Color.WHITE)
        title_bar!!.setDividerColor(Color.GRAY)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}
