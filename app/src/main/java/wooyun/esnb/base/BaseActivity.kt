package wooyun.esnb.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayoutId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(supportActionBar)!!.hide()
        setContentView(getLayoutId())
    }

}