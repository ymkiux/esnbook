package wooyun.esnb.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import wooyun.esnb.R
import wooyun.esnb.adapter.KyAdapter
import wooyun.esnb.bean.Ky
import wooyun.esnb.interfaces.AppBarStateChangeListener
import wooyun.esnb.util.Tools
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class KyActivity : AppCompatActivity() {
    private var mLastClickTime: Long = 0
    private var mtl1: PhotoView? = null
    private var imageUrl = "https://api.ixiaowai.cn/api/api.php"
    var button: FloatingActionButton? = null
    private var list: MutableList<Ky>? = ArrayList<Ky>()
    private var toolbar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(supportActionBar)!!.hide()
        setContentView(R.layout.activity_ky)
        initView()
        val string = Tools().spGet(this, "wooyun.notepad_preferences").getString("edit_pt", "")
        if (string != null && string.length != 0) imageUrl = string
        mtl1 = findViewById(R.id.cat_image_view)
        mtl1!!.enable()
        val a = AnimationUtils.loadAnimation(this@KyActivity, R.anim.jianbian)
        mtl1!!.startAnimation(a)
        Glide.with(this).load(imageUrl).into(mtl1!!)
        button = findViewById(R.id.btn)
        button!!.setOnClickListener(View.OnClickListener { v: View? ->
            val nowTime = System.currentTimeMillis()
            if (nowTime - mLastClickTime > TIME_INTERVAL) {
                mLastClickTime = nowTime
                //新建线程加载图片信息，发送到消息队列中
                Thread(Runnable {
                    try {
                        val bmp = get_URLimage(imageUrl)
                        val msg = Message()
                        msg.what = 0
                        msg.obj = bmp
                        handle.sendMessage(msg)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@KyActivity, R.string.wrong, Toast.LENGTH_SHORT).show()
                    }
                }).start()
            } else {
                Toast.makeText(this@KyActivity, R.string.soureworng, Toast.LENGTH_SHORT).show()
            }
        })
        mtl1!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                try {
                    mtl1!!.buildDrawingCache(true)
                    mtl1!!.buildDrawingCache()
                    val bitmap = (mtl1!!.drawable as BitmapDrawable).bitmap
                    saveBitmapFile(bitmap)
                    mtl1!!.isDrawingCacheEnabled = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@KyActivity, R.string.errornull, Toast.LENGTH_SHORT).show()
                }
            }

            private fun saveBitmapFile(bitmap: Bitmap) {
                @SuppressLint("SdCardPath") val temp = File(getExternalFilesDir("").path + "/image/") //要保存文件先创建文件夹
                if (!temp.exists()) {
                    temp.mkdir()
                }
                /*重复保存时，覆盖原同名图片(本思维逻辑下不存在这个ai图像相似排除)*/
                val times = Tools.getTime();
                Toast.makeText(this@KyActivity, "已保存至/image/$times.png", Toast.LENGTH_SHORT).show()
                /*将要保存图片的路径和图片名称*/
                @SuppressLint("SdCardPath") val file = File(temp.toString() + "/" +
                        times + ".png")
                try {
                    val bos = BufferedOutputStream(FileOutputStream(file))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    bos.flush()
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }


    override fun onStart() {
        super.onStart()
        if (list!!.size != 0) list!!.clear()
        val recycle_activity_ky_info = findViewById<RecyclerView>(R.id.recycle_activity_ky_infos)
        list!!.add(Ky("jsoup", "jsoup is a Java HTML parser that can directly parse a URL address and HTML text content. It provides a very labor-saving API,Data can be retrieved and manipulated through DOM, CSS and operation methods similar to jQuery", "https://jsoup.org/"))
        list!!.add(Ky("photoview", "photoview is An ImageView display frame that supports zooming, responds to gestures", "https://github.com/bm-x/PhotoView/"))
        list!!.add(Ky("glide", "Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface", "https://github.com/bumptech/glide"))
        list!!.add(Ky("tools", "tools is an open source library by me, adapted to Android", "https://github.com/ymkiux/tools"))
        val layoutManager = LinearLayoutManager(this)
        recycle_activity_ky_info.layoutManager = layoutManager
        val kyAdapter = KyAdapter(this)
        kyAdapter.setData(list)
        recycle_activity_ky_info.adapter = kyAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.app_bar_layout)
        appBarLayout!!.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.EXPANDED) {
                    /*展开状态*/
                    /*toolbar.setTitle("");*/
                    toolbar!!.visibility = View.GONE
                } else if (state == State.COLLAPSED) {
                    /*折叠状态*/
                    toolbar!!.visibility = View.VISIBLE
                } else {
                    /*中间状态*/
                    toolbar!!.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun get_URLimage(imageUrl: String): Bitmap? {
        var bmp: Bitmap? = null
        try {
            val myurl = URL(imageUrl)
            // 获得连接
            val conn = myurl.openConnection() as HttpURLConnection
            conn.connectTimeout = 6000 //设置超时
            conn.doInput = true
            conn.useCaches = true //缓存
            conn.connect()
            val `is` = conn.inputStream //获得图片的数据流
            bmp = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bmp
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@KyActivity, AboutActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    //在消息队列中实现对控件的更改
    @SuppressLint("HandlerLeak")
    private val handle: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    val bmp = msg.obj as Bitmap
                    mtl1!!.setImageBitmap(bmp)
                }
            }
        }
    }

    companion object {
        const val TIME_INTERVAL = 1000L
    }
}

