package wooyun.esnb.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_about.*
import wooyun.esnb.R
import wooyun.esnb.base.BaseActivity
import wooyun.esnb.fragment.AboutFragment
import wooyun.esnb.fragment.KyFragment
import wooyun.esnb.fragment.PictureFragment
import wooyun.esnb.interfaces.onBackPressed


class MActivity : BaseActivity() {

    private var backPressed: onBackPressed? = null
    private var isInterception = false
    private var intExtra: Int = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intExtra = intent.getIntExtra("Fragment", 0)
        initView()
        initData()
    }

    private fun initData() {
        when (intExtra) {
            0 -> supportFragmentManager
                    .beginTransaction()
                    .add(R.id.frag_module, AboutFragment(), null)
                    .commit()
            1 -> {
                title_bar.visibility = View.GONE
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.frag_module, PictureFragment(), null)
                        .commit()
            }
            2 -> {
                title_bar.visibility = View.GONE
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.frag_module, KyFragment(), null)
                        .commit()
            }
        }
    }


    private fun initView() {
        if (MainActivity.hasKitKat() && !MainActivity.hasLollipop()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else if (MainActivity.hasLollipop()) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        title_bar!!.setTitle("")
        title_bar!!.setTitleColor(Color.WHITE)
        title_bar!!.setSubTitleColor(Color.WHITE)
        title_bar!!.setDividerColor(Color.GRAY)
    }


    fun setBackListener(backPressed: onBackPressed?) {
        this.backPressed = backPressed
    }

    fun isInterception(): Boolean {
        return isInterception
    }

    fun setInterception(isInterception: Boolean) {
        this.isInterception = isInterception
    }

    override fun onBackPressed() {
        when (intExtra) {
            0 -> {
                if (isInterception()) {
                    if (backPressed != null) {
                        backPressed!!.onBackForward()
                        finish()
                    }
                }
            }
            1 -> {
                val intent = Intent(this, MActivity::class.java)
                intent.putExtra("Fragment", 2)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
            2 -> {
                val intent = Intent(this, MActivity::class.java)
                intent.putExtra("Fragment", 0)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }
        super.onBackPressed()
    }
}