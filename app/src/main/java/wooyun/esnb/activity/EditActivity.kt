package wooyun.esnb.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import wooyun.esnb.R
import wooyun.esnb.bean.Values
import wooyun.esnb.cursom.TitleBar
import wooyun.esnb.sql.DbOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    var myDb: DbOpenHelper? = null
    private val btnCancel: Button? = null
    private var titleEditText: EditText? = null
    private var contentEditText: EditText? = null
    private var timeTextView: TextView? = null
    private val value: Values? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //remove title bar  即隐藏标题栏
        Objects.requireNonNull(supportActionBar)!!.hide() // 隐藏ActionBar
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) //让布局向上移来显示软键盘
        setContentView(R.layout.activity_editor)
        init()
        val et = findViewById<EditText>(R.id.et_title) //设置光标
        et.isCursorVisible = false /*设置光标不显示*/
        val et1 = findViewById<EditText>(R.id.et_content)
        et1.setSelectAllOnFocus(true) /*获得焦点时全选文本*/
        if (timeTextView!!.text.length == 0) timeTextView!!.text = time
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et1.windowToken, 0)
        var isImmersive = false
        if (MainActivity.hasKitKat() && !MainActivity.hasLollipop()) {
            isImmersive = true
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (MainActivity.hasLollipop()) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            isImmersive = true
        }
        val titleBar = findViewById<View>(R.id.title_bar) as TitleBar
        titleBar.setTitle("")
        titleBar.setTitleColor(Color.WHITE)
        titleBar.setSubTitleColor(Color.WHITE)
    }

    private fun init() {
        myDb = DbOpenHelper(this)
        val db = myDb!!.readableDatabase
        titleEditText = findViewById(R.id.et_title)
        contentEditText = findViewById(R.id.et_content)
        timeTextView = findViewById(R.id.edit_time)
        val btnSave = findViewById<FloatingActionButton>(R.id.btn_save)
        btnSave.setOnClickListener { v: View? ->
            val db1 = myDb!!.writableDatabase
            val values = ContentValues()
            val title = titleEditText!!.text.toString()
            val content = contentEditText!!.text.toString()
            val time = timeTextView!!.text.toString()
            if ("" == titleEditText!!.text.toString() || "" == contentEditText!!.text.toString()) {
                /* Toasty.warning(EditActivity.this, "标题或者内容不能为空", Toast.LENGTH_SHORT, true).show();
                 */
                Toast.makeText(this@EditActivity, "标题或者内容不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            values.put(DbOpenHelper.TITLE, title)
            values.put(DbOpenHelper.CONTENT, content)
            values.put(DbOpenHelper.TIME, time)
            db1.insert(DbOpenHelper.TABLE, null, values)
            Toast.makeText(this@EditActivity, "保存成功!", Toast.LENGTH_SHORT).show()
            /*Toasty.success(EditActivity.this, "保存成功!", Toast.LENGTH_SHORT, true).show();
             */
            val intent = Intent(this@EditActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            db1.close()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    //获取当前时间
    private val time: String
        private get() {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(System.currentTimeMillis())
            return sdf.format(date)
        }

    //物理返回键重写事件
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            save_dismiss()
        }
        return true
    }

    private fun save_dismiss() {
        val db = myDb!!.writableDatabase
        val values = ContentValues()
        val title = titleEditText!!.text.toString()
        val content = contentEditText!!.text.toString()
        val time = timeTextView!!.text.toString()
        if ("" == titleEditText!!.text.toString() || "" == contentEditText!!.text.toString()) {
            startActivity(Intent(this@EditActivity, MainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            Toast.makeText(this@EditActivity, "无内容", Toast.LENGTH_SHORT).show()
            return
        }
        values.put(DbOpenHelper.TITLE, title)
        values.put(DbOpenHelper.CONTENT, content)
        values.put(DbOpenHelper.TIME, time)
        db.insert(DbOpenHelper.TABLE, null, values)
        Toast.makeText(this@EditActivity, "保存成功.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@EditActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
        db.close()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
