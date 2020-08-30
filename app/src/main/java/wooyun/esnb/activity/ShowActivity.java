package wooyun.esnb.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import wooyun.esnb.R;
import wooyun.esnb.bean.Values;
import wooyun.esnb.cursom.TitleBar;
import wooyun.esnb.dialog.Popupwindow;
import wooyun.esnb.sql.DbOpenHelper;

import static wooyun.esnb.activity.MainActivity.hasKitKat;
import static wooyun.esnb.activity.MainActivity.hasLollipop;

public class ShowActivity extends AppCompatActivity {

    private FloatingActionButton btnSave;
    private EditText showContent;
    private EditText showTitle;
    Popupwindow menuWindow;
    private Values value;
    DbOpenHelper myDb;
    private GestureDetector gestureDetector;
    //记录输入的字数
    private CharSequence wordNum;
    private TextView showNumber;
    private boolean btnSave_show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//让布局向上移来显示软键盘
        setContentView(R.layout.activity_show);
        init();
        initView();
       /* EditText et = findViewById(R.id.show_title);//设置光标不显示,但不能设置光标颜色
        et.setCursorVisible(false);
        EditText et1 = findViewById(R.id.show_content);
        et1.setSelectAllOnFocus(true);*/
        boolean isImmersive = false;
        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }
        final TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle("");
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.WHITE);
    }

    private void initView() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityY) < 100) {
                    return true;
                }
                //手势向下
                if ((e2.getRawY() - e1.getRawY()) > 20) {
                    startActivity(new Intent(ShowActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public void init() {
        myDb = new DbOpenHelper(this);
        btnSave = findViewById(R.id.btn_save1);
        TextView showTime = findViewById(R.id.show_time);
        showTitle = findViewById(R.id.show_title);
        showContent = findViewById(R.id.show_content);
        showNumber = findViewById(R.id.tv_showNumber);
        btnSave.setOnLongClickListener(new floatStr());
        showContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //实时记录输入的字数
                wordNum = charSequence;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                int number = editable.length();
                showNumber.setText("字数：" + number);
                /*selectionStart = showContent.getSelectionStart();
                selectionEnd = showContent.getSelectionEnd();
                if (temp.length() > num) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    content.setText(s);
                    content.setSelection(tempSelection);//设置光标在最后
                }
*/
            }
        });
        Intent intent = this.getIntent();
        if (intent != null) {
            value = new Values();

            value.setTime(intent.getStringExtra(DbOpenHelper.TIME));
            value.setTitle(intent.getStringExtra(DbOpenHelper.TITLE));
            value.setContent(intent.getStringExtra(DbOpenHelper.CONTENT));
            value.setId(Integer.valueOf(intent.getStringExtra(DbOpenHelper.ID)));

            showTime.setText(value.getTime());
            showTitle.setText(value.getTitle());
            showContent.setText(value.getContent());
        }

        //按钮点击事件
        btnSave.setOnClickListener(v -> {
            //实例化SelectPicPopupWindow
            menuWindow = new Popupwindow(ShowActivity.this, itemsOnClick);
            //显示窗口
            menuWindow.showAtLocation(ShowActivity.this.findViewById(R.id.main1), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

        });
    }

    String getTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    //物理返回键重写事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
        }
        return true;
    }

    private void showDialog() {
        final String content = showContent.getText().toString();
        final String title = showTitle.getText().toString();
        SQLiteDatabase db = myDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.TIME, getTime());
        values.put(DbOpenHelper.TITLE, title);
        values.put(DbOpenHelper.CONTENT, content);
        db.update(DbOpenHelper.TABLE, values, DbOpenHelper.ID + "=?", new String[]{value.getId().toString()});
        db.close();
        Intent sEIntent = new Intent(ShowActivity.this, MainActivity.class);
        startActivity(sEIntent);
        finish();
     /*R.anim.enter:新的Activity进入时的动画
    R.anim.edit：旧的Activity出去时的动画*/
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Toast.makeText(ShowActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_bianhua:
                    SQLiteDatabase db = myDb.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    String content = showContent.getText().toString();
                    String title = showTitle.getText().toString();

                    //检索内容
                    String search_contet = "3255284101";
                    boolean i=content.contains(search_contet);
                    if (i) {
                         Toast.makeText(ShowActivity.this, getString(R.string.input_suspicious), Toast.LENGTH_SHORT).show();
                    }else {
                        try {
                            values.put(DbOpenHelper.TIME, getTime());
                            values.put(DbOpenHelper.TITLE, title);
                            values.put(DbOpenHelper.CONTENT, content);

                            db.update(DbOpenHelper.TABLE, values, DbOpenHelper.ID + "=?", new String[]{value.getId().toString()});
                           /* Toasty.success(ShowActivity.this, "修改成功!", Toast.LENGTH_SHORT, true).show();
                           */ Toast.makeText(ShowActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            db.close();
                        }
                    }
                    break;
                case R.id.btn_dchu:
                    String fileName = showTitle.getText().toString().trim();
                    String fileText = showContent.getText().toString().trim();
                    String f1 = "标题内容：" + fileName + "\n" + "正文内容：" + fileText;
                    String s1 = fileName + ".txt";
                    WriteDataToStorage(f1, TestFilePathApkPrivate(getApplicationContext()), s1);
                   /* Toasty.warning(ShowActivity.this, "导出到Internal storage/Android/data/wooyun.notepad/files/SQLite/" + fileName + ".txt文件中", Toast.LENGTH_SHORT, true).show();
                    */Toast.makeText(ShowActivity.this, "导出到/txt/" + fileName + ".txt文件中!", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent();
                    intent1.setClass(ShowActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                default:
                    break;
            }
        }
    };
    private String LOG_Info = "dachenI";
    private String LOG_Debug = "dachenD";
    private String LOG_Error = "dachenE";

    private String TestFilePathApkPrivate(Context context) {
        //不需要挂载测试，因为 app 都可以装 为什么 会没有数据
        String filedirpath = Objects.requireNonNull(context.getExternalFilesDir("txt")).getPath();  //文件夹
        File fileDir = new File(filedirpath);                   //创建文件夹
        if (fileDir.exists()) {    //判断文件是否存在  很重要  别每次都会去覆盖数据
            fileDir.setWritable(true);
            Log.i(LOG_Info, "文件夹已经存在    TestFilePathInternalData（）");
        } else {
            try {
                fileDir.mkdir();
                Log.i(LOG_Info, "文件夹创建成功    TestFilePathExternalData（）");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, "文件夹创建错误   TestFilePathExternalData()" + e.getMessage());
            }
        }
        return filedirpath;
    }

    @SuppressLint({"WorldReadableFiles", "WorldWriteableFiles"})
    private void WriteDataToStorage(String content, String filedirname, String filename) {
        String FileName = filedirname + File.separator + filename;   //拼接字符串  文件的存储路径
        File subfile = new File(FileName);  //文件夹路径和文件路径   判断文件是否存在
        if (subfile.exists()) {
            subfile.setWritable(true);
            boolean readable = subfile.canRead();
            boolean writeable = subfile.canWrite();
            Log.i(LOG_Info, "文件创建成功" + "readable:" + readable + " writeable:" + writeable);
        } else {
            try {
                subfile.createNewFile();
            } catch (IOException e) {
                Log.i(LOG_Error, "文件创建出错  " + e.getMessage());
                e.printStackTrace();
            }
        }
        int Context_Mode = 3;
        int Ways = 0;
        if (Context_Mode == 0) {
            Context_Mode = Context.MODE_PRIVATE;  //该文件只能被当前程序读写。
        } else if (Context_Mode == 1) {
            Context_Mode = Context.MODE_APPEND;   //以追加方式打开该文件，应用程序可以向该文件中追加内容。
        } else if (Context_Mode == 2) {
            Context_Mode = Context.MODE_WORLD_READABLE;  //该文件的内容可以被其他应用程序读取。
        } else if (Context_Mode == 3) {
            Context_Mode = MODE_WORLD_WRITEABLE;  //该文件的内容可由其他程序读、写。
        } else {
            Context_Mode = MODE_WORLD_WRITEABLE;  //省的烦   反正都可以读
        }
        if (Ways == 0) {
            Log.i(LOG_Info, "BufferWriter");
            FileOutputStream fileOutputStream = null;
            BufferedWriter bufferedWriter = null;
            OutputStreamWriter outputStreamWriter = null;
            try {
                //fileOutputStream = openFileOutput(FileName, Context_Mode);  contains a path separator 报错
                fileOutputStream = new FileOutputStream(subfile);
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "utf-8"));  //解决输入中文的问题
                bufferedWriter.write(content + "\t");
                bufferedWriter.flush();
                bufferedWriter.close();
                //outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");   //两种方式都可以
                //outputStreamWriter.write(content);
                //outputStreamWriter.flush();
                //outputStreamWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, "写入数据出错 " + e.getMessage());
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (Ways == 1) {
            Log.i(LOG_Info, "RandomAccessFile");
            try {
                RandomAccessFile raf = new RandomAccessFile(subfile, "rw");
                raf.seek(subfile.length());
                raf.write(content.getBytes());
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_Error, "写入数据出错 " + e.getMessage());
            }
        } else if (Ways == 2) {
            Log.i(LOG_Info, "Printer");
            try {
                FileOutputStream fileoutputStream = new FileOutputStream(subfile);
                //openFileOutput("text2", Context.MODE_PRIVATE);
                PrintStream ps = new PrintStream(fileoutputStream);
                ps.print(content + "\t");
                ps.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else
            Ways = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class floatStr implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            if (btnSave_show) {// 已显示悬浮按钮
                btnSave.hide();
            }
            btnSave_show = !btnSave_show;
            return false;
        }
    }
}
