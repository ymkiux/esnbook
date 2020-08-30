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
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import wooyun.esnb.R
import wooyun.esnb.cursom.AppBarStateChangeListener
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class KyActivity : AppCompatActivity() {
    private var mLastClickTime: Long = 0
    private var mtl1: PhotoView? = null
    private var l1: LinearLayout? = null
    private val l2: LinearLayout? = null
    private var l3: LinearLayout? = null
    private var l4: LinearLayout? = null
    private val imageUrl = "https://api.ixiaowai.cn/api/api.php"
    var button: FloatingActionButton? = null
    private var toolbar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Objects.requireNonNull(supportActionBar)!!.hide()
        setContentView(R.layout.activity_ky)
        initView()
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
                /**
                 * 通过的format方法转为字符串类型
                 */
                val date = Date()
                @SuppressLint("SimpleDateFormat") val time1 = SimpleDateFormat("yyyy")
                @SuppressLint("SimpleDateFormat") val time2 = SimpleDateFormat("MM")
                @SuppressLint("SimpleDateFormat") val time3 = SimpleDateFormat("dd")
                @SuppressLint("SimpleDateFormat") val time4 = SimpleDateFormat("HH")
                @SuppressLint("SimpleDateFormat") val time5 = SimpleDateFormat("mm")
                @SuppressLint("SimpleDateFormat") val time6 = SimpleDateFormat("ss")
                val times = time1.format(date) + time2.format(date) + time3.format(date) + time4.format(date) + time5.format(date) + time6.format(date)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        l1 = findViewById(R.id.clickTouch)
        l3 = findViewById(R.id.clickTouch2)
        l4 = findViewById(R.id.clickTouch3)
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

    /*public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            new Thread(() -> {
                if (!checkIsVisible(KyActivity.this, button)) {
                    toolbar.setTitle(R.string.ky);
                }else{
                    toolbar.setTitle("");
                }
            }).start();

        }
    }*/
    /*  public static Boolean checkIsVisible(Context context, View view) {
     */
    /* 如果已经加载了，判断是否显示出来*/ /*
        int screenWidth = getScreenMetrics(context).x;
        int screenHeight = getScreenMetrics(context).y;
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (view.getLocalVisibleRect(rect)) {
            return true;
        } else {
            */
    /*不可见区域*/ /*
            return false;
        }
    }

    private static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);
    }
*/
    /* private class OnToiu implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    switch (v.getId()) {
                        case R.id.clickTouch:
                            l1.setBackgroundResource(R.drawable.backtouch);
                            break;
                        case R.id.clickTouch2:
                            l3.setBackgroundResource(R.drawable.backtouch);
                            break;
                        case R.id.clickTouch3:
                            l4.setBackgroundResource(R.drawable.backtouch);
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    switch (v.getId()) {
                        case R.id.clickTouch:
                            l1.setBackgroundResource(R.drawable.layer);
                            break;
                        case R.id.clickTouch2:
                            l3.setBackgroundResource(R.drawable.layer);
                            break;
                        case R.id.clickTouch3:
                            l4.setBackgroundResource(R.drawable.layer);
                            break;
                    }
                    break;

            }
            return true;
        }
    }*/
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

    //物理返回键重写事件
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@KyActivity, AboutActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        return true
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

