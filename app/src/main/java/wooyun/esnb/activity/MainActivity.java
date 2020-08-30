package wooyun.esnb.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import wooyun.esnb.R;
import wooyun.esnb.sql.DbOpenHelper;
import wooyun.esnb.bean.Values;
import wooyun.esnb.util.CacheDataManager;
import wooyun.esnb.cursom.TitleBar;
import wooyun.esnb.util.Utils;
import wooyun.esnb.dialog.SelectPicPopupWindow;


public class MainActivity extends AppCompatActivity {
    DbOpenHelper myDb;
    private ListView lv_note;
    private SwipeRefreshLayout swipeRefresh;
    SelectPicPopupWindow menuWindow;
    private Snackbar snackbar = null;
    private TitleBar titleBar;
    private static Boolean isExit = false;
    private static final String TAG_EXIT = "exit";
    private SharedPreferences prefs;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar
        setContentView(R.layout.activity_main);
        myDb = new DbOpenHelper(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
       /* home_t = prefs.getBoolean("home_t", false);
        讀取key參數*/
        init();
        isFirstIn();
        boolean isImmersive = false;
        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this::refresh);
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("");
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.WHITE);
        titleBar.setDividerColor(Color.GRAY);
        /*下划分割线*/
    }

    private void isFirstIn() {
        SharedPreferences pref = this.getSharedPreferences("isFirstIn", MODE_PRIVATE);
        //取得相应的值，如果没有该值， 说明还未写入，用true作为默认值
        boolean isFirstIn = pref.getBoolean("isFirstIn", true);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstIn", false);
        editor.apply();
        if (isFirstIn) {
            Show_FirstDialog();
        }
    }

    private void Show_FirstDialog() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_md, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        WebView mWebView = view.findViewById(R.id.markdownView);
        mWebView.loadUrl("file:///android_asset/updateLog.html");
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        Objects.requireNonNull(dialog.getWindow()).setLayout((Utils.getScreenWidth(this) / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    /**
     * 定时Timer
     * public void isIntent() {
     * boolean vpnUsed = Utils.isVpnUsed();
     * if (vpnUsed) {
     * Toasty.error(MainActivity.this, "检测到VPN服务", 0).show();
     * Timer timer = new Timer();
     * TimerTask timerTask = new TimerTask() {
     *
     * @Override public void run() {
     * finish();
     * }
     * };
     * timer.schedule(timerTask, 1000L);
     * }
     * }
     */

    public void init() {
        /*try {
            Thread_del();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*try {
            Thread_home();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        FloatingActionButton mBtnAdd = findViewById(R.id.btn_add);
        lv_note = findViewById(R.id.lv_note);
        lv_note.setEmptyView(findViewById(android.R.id.empty));
        List<Values> valuesList = new ArrayList<>();
        SQLiteDatabase db = myDb.getReadableDatabase();

        //查询数据库中的数据
        Cursor cursor = db.query(DbOpenHelper.TABLE, null, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            Values values;
            while (!cursor.isAfterLast()) {
                //实例化values对象
                values = new Values();

                //把数据库中的一个表中的数据赋值给values
                values.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(DbOpenHelper.ID))));
                values.setTitle(cursor.getString(cursor.getColumnIndex(DbOpenHelper.TITLE)));
                values.setContent(cursor.getString(cursor.getColumnIndex(DbOpenHelper.CONTENT)));
                values.setTime(cursor.getString(cursor.getColumnIndex(DbOpenHelper.TIME)));

                //将values对象存入list对象数组中
                valuesList.add(values);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        //设置list组件adapter
        final MyBaseAdapter myBaseAdapter = new MyBaseAdapter(valuesList, this, R.layout.activity_item);
        lv_note.setAdapter(myBaseAdapter);
        mBtnAdd.setOnClickListener(v -> {
            try {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mBtnAdd.setOnLongClickListener(v -> {
            mBtnAdd.hide();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return false;
        });
        //单击查询
        lv_note.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ShowActivity.class);
            Values values = (Values) lv_note.getItemAtPosition(position);
            intent.putExtra(DbOpenHelper.TITLE, values.getTitle().trim());
            intent.putExtra(DbOpenHelper.CONTENT, values.getContent().trim());
            intent.putExtra(DbOpenHelper.TIME, values.getTime().trim());
            intent.putExtra(DbOpenHelper.ID, values.getId().toString().trim());
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


        //双击删除
        lv_note.setOnItemLongClickListener((parent, view, position, id) -> {
            final Values values = (Values) lv_note.getItemAtPosition(position);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("")
                    .setMessage("是否删除?")
                    .setPositiveButton("确定",
                            (dialog, which) -> {
                                try {
                                    SQLiteDatabase db1 = myDb.getWritableDatabase();
                                    db1.delete(DbOpenHelper.TABLE, DbOpenHelper.ID + "=?", new String[]{String.valueOf(values.getId())});
                                    db1.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                myBaseAdapter.removeItem(position);
                                lv_note.post(myBaseAdapter::notifyDataSetChanged);
                                //MainActivity.this.onResume();
                            })
                    .setNegativeButton("取消", null).show();
            return true;
        });
    }

    class MyBaseAdapter extends BaseAdapter {

        private List<Values> valuesList;
        private Context context;
        private int layoutId;

        public MyBaseAdapter(List<Values> valuesList, Context context, int layoutId) {
            this.valuesList = valuesList;
            this.context = context;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            if (valuesList != null && valuesList.size() > 0)
                return valuesList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if (valuesList != null && valuesList.size() > 0)
                return valuesList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.activity_item, parent,
                        false);
                viewHolder = new ViewHolder();
                try {
                    viewHolder.title = convertView.findViewById(R.id.tv_title);
                    viewHolder.content = convertView.findViewById(R.id.tv_content);
                    viewHolder.time = convertView.findViewById(R.id.tv_time);
                    convertView.setTag(viewHolder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                String title = valuesList.get(position).getTitle();
                String content = valuesList.get(position).getContent();
                viewHolder.title.setText(title);
                viewHolder.content.setText(content);
                convertView.setBackgroundResource(R.drawable.item_shape);
                viewHolder.time.setText(valuesList.get(position).getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        public void removeItem(int position) {
            this.valuesList.remove(position);
        }

    }

    class ViewHolder {
        TextView title;
        TextView content;
        TextView time;
    }

    private long exitTime;

    /*重写返回键，实现双击退出效果*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Timer tExit = null;
            if (!isExit) {
                isExit = true;
                showSnackBar(titleBar, getString(R.string.click_exit));
                tExit = new Timer();
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);

            } else {
                boolean del_t = prefs.getBoolean("del_t", false);
                if (del_t) {
                    new Thread(new clearCache()).start();
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.TAG_EXIT, true);
                startActivity(intent);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

 /*   private void Thread_del() throws IOException {

               File del_file=getApplication().getFilesDir();
               File file = new File(del_file,"data.txt");
                FileInputStream fileInputStream=new FileInputStream(file);
                byte[] buffer=new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                del=new String(buffer);
                fileInputStream.close();
            }
    private void Thread_home() throws IOException {

        File home_file=getApplication().getFilesDir();
        File file1 = new File(home_file,"home.txt");
        FileInputStream fileInputStream=new FileInputStream(file1);
        byte[] buffer=new byte[fileInputStream.available()];
        fileInputStream.read(buffer);
        home=new String(buffer);
        fileInputStream.close();
    }*/


    /*下拉刷新*/
    @SuppressLint("Recycle")
    private void refresh() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
               /* SQLiteDatabase db = myDb.getWritableDatabase();
                db.query(DBService.TABLE, null, null, null, null, null, null);
                db.close();*/
               /* Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(MainActivity.this, MainActivity.class);
                startActivity(intent);*/
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            runOnUiThread(() -> {
                init();
                swipeRefresh.setRefreshing(false);
            });
        }).start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }


    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_write:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, EditActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case R.id.btn_about:
                    Intent intent1 = new Intent();
                    intent1.setClass(MainActivity.this, AboutActivity.class);
                    startActivity(intent1);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case R.id.btn_setting:
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                default:
                    break;
            }
        }
    };



    /*private void setLayoutAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.list_layout);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setDelay(0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lv_note.setLayoutAnimation(controller);
    }*/

    private void showSnackBar(View view, String text) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        } else {
            snackbar.setText(text);
            snackbar.setDuration(Snackbar.LENGTH_SHORT);
        }
        snackbar.show();
    }

    private class clearCache implements Runnable {

        @Override

        public void run() {
            try {
                CacheDataManager.clearAllCache(MainActivity.this);
                Thread.sleep(3000);
                if (CacheDataManager.getTotalCacheSize(MainActivity.this).startsWith("0")) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception ignored) {
            }

        }

    }
}
