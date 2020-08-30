package wooyun.esnb.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import wooyun.esnb.R;
import wooyun.esnb.cursom.AppBarStateChangeListener;


public class KyActivity extends AppCompatActivity {
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;
    private PhotoView mtl1;
    private LinearLayout l1, l2, l3, l4;
    private String imageUrl = "https://api.ixiaowai.cn/api/api.php";
    FloatingActionButton button;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_ky);
        initView();
        mtl1 = findViewById(R.id.cat_image_view);
        mtl1.enable();
        Animation a = AnimationUtils.loadAnimation(KyActivity.this, R.anim.jianbian);
        mtl1.startAnimation(a);

        Glide.with(this).load(imageUrl).into(mtl1);
        button = findViewById(R.id.btn);
        button.setOnClickListener(v -> {
            long nowTime = System.currentTimeMillis();
            if (nowTime - mLastClickTime > TIME_INTERVAL) {
                mLastClickTime = nowTime;
                //新建线程加载图片信息，发送到消息队列中
                new Thread(() -> {
                    try {
                        Bitmap bmp = get_URLimage(imageUrl);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = bmp;
                        handle.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(KyActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                    }
                }).start();
            } else {
                Toast.makeText(KyActivity.this, R.string.soureworng, Toast.LENGTH_SHORT).show();
            }
        });
        mtl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mtl1.buildDrawingCache(true);
                    mtl1.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) mtl1.getDrawable()).getBitmap();
                    saveBitmapFile(bitmap);
                    mtl1.setDrawingCacheEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(KyActivity.this, R.string.errornull, Toast.LENGTH_SHORT).show();
                }
            }

            private void saveBitmapFile(Bitmap bitmap) {
                @SuppressLint("SdCardPath") File temp = new File(getExternalFilesDir("").getPath() + "/image/");//要保存文件先创建文件夹
                if (!temp.exists()) {
                    temp.mkdir();
                }
                /*重复保存时，覆盖原同名图片(本思维逻辑下不存在这个ai图像相似排除)*/
                /**
                 *  通过的format方法转为字符串类型
                 */
                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat time1 = new SimpleDateFormat("yyyy");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat time2 = new SimpleDateFormat("MM");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat time3 =
                        new SimpleDateFormat("dd");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat time4 = new SimpleDateFormat("HH");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat time5 = new SimpleDateFormat("mm");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat time6 = new SimpleDateFormat("ss");
                String times = time1.format(date) + time2.format(date) + time3.format(date) + time4.format(date) + time5.format(date) + time6.format(date);
                Toast.makeText(KyActivity.this, "已保存至/image/" + times + ".png", Toast.LENGTH_SHORT).show();
                /*将要保存图片的路径和图片名称*/
                @SuppressLint("SdCardPath") File file = new File(temp + "/" +
                        times + ".png");
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        l1 = findViewById(R.id.clickTouch);
        l3 = findViewById(R.id.clickTouch2);
        l4 = findViewById(R.id.clickTouch3);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    /*展开状态*/
                    /*toolbar.setTitle("");*/
                    toolbar.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    /*折叠状态*/
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    /*中间状态*/
                    toolbar.setVisibility(View.VISIBLE);

                }
            }
        });

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
     *//* 如果已经加载了，判断是否显示出来*//*
        int screenWidth = getScreenMetrics(context).x;
        int screenHeight = getScreenMetrics(context).y;
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (view.getLocalVisibleRect(rect)) {
            return true;
        } else {
            *//*不可见区域*//*
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

    private Bitmap get_URLimage(String imageUrl) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(imageUrl);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(true);//缓存
            conn.connect();
            while ("".equals(conn)) return null;
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;

    }

    //物理返回键重写事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(KyActivity.this, AboutActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return true;
    }

    //在消息队列中实现对控件的更改
    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bitmap bmp = (Bitmap) msg.obj;
                    mtl1.setImageBitmap(bmp);
                    break;
            }
        }
    };
}

