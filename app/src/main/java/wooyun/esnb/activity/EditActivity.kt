package wooyun.esnb.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_editor.*
import wooyun.esnb.R
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.room.Note
import wooyun.esnb.room.NoteController
import wooyun.esnb.util.Tools
import wooyun.esnb.view.TitleBar
import java.util.*

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //remove title bar  即隐藏标题栏
        Objects.requireNonNull(supportActionBar)!!.hide() // 隐藏ActionBar
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) //让布局向上移来显示软键盘
        setContentView(R.layout.activity_editor)
        et_title.isCursorVisible = false /*设置光标不显示*/
        val et1 = findViewById<EditText>(R.id.et_content)
        et1.setSelectAllOnFocus(true) /*获得焦点时全选文本*/
        if (edit_time!!.text.length == 0) edit_time!!.text = Tools.getTime(System.currentTimeMillis())
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et1.windowToken, 0)
        val titleBar = findViewById<View>(R.id.title_bar) as TitleBar
        titleBar.setTitle("")
        titleBar.setTitleColor(Color.WHITE)
        titleBar.setSubTitleColor(Color.WHITE)
    }

    override fun onStart() {
        super.onStart()
        btn_save.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                addData(false)
            }
        })
    }

    private fun addData(tag:Boolean) {
        when(tag){
            true->{
                if ("" == et_title.text.toString() || "" == et_content.text.toString()) {
                    startActivity(Intent(this@EditActivity, MainActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    Tools().showToast(this@EditActivity, "无内容")
                    return
                }
            }
            false->{
                if ("" == et_title.text.toString() || "" == et_content.text.toString()) {
                    Tools().showToast(this@EditActivity, "标题或者内容不能为空!")
                    return
                }
            }
        }
        NoteController(this@EditActivity).init().add(Note(et_title.text.toString(), et_content.text.toString(), System.currentTimeMillis().toString()))
        Tools().showToast(this@EditActivity, "保存成功!")
        val intent = Intent(this@EditActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    //物理返回键重写事件
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            addData(true)
        }
        return true
    }
}
