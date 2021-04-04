package wooyun.esnb.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import wooyun.esnb.R;
import wooyun.esnb.dialog.Popupwindow;
import wooyun.esnb.room.Note;
import wooyun.esnb.room.NoteController;
import wooyun.esnb.util.Tools;
import wooyun.esnb.view.TitleBar;


public class ShowActivity extends AppCompatActivity {

    private FloatingActionButton btnSave;
    private EditText showContent;
    private EditText showTitle;
    Popupwindow menuWindow;
    private GestureDetector gestureDetector;
    //记录输入的字数
    private CharSequence wordNum;
    private TextView showNumber;
    private boolean btnSave_show = true;

    private Note note;

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
            }
        });

        Intent intent = this.getIntent();
        if (intent != null) {
            note = intent.getParcelableExtra("noteID");
            showTime.setText(Tools.Companion.getTime(Long.parseLong(note.getTime())));
            showTitle.setText(note.getTitle());
            showContent.setText(note.getContext());
        }

        //按钮点击事件
        btnSave.setOnClickListener(v -> {
            //实例化SelectPicPopupWindow
            menuWindow = new Popupwindow(ShowActivity.this, itemsOnClick);
            //显示窗口
            menuWindow.showAtLocation(ShowActivity.this.findViewById(R.id.main1), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

        });
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
        Note notes = new Note(title, content, ShowActivity.this.note.getTime());
        notes.setId(note.getId());
        new NoteController(ShowActivity.this).init().update(note,title,content);
        update();
    }


    private void update() {
        Intent sEIntent = new Intent(ShowActivity.this, MainActivity.class);
        startActivity(sEIntent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        new Tools().showToast(ShowActivity.this, "修改成功");
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            String fileName = showTitle.getText().toString().trim();
            String fileText = showContent.getText().toString().trim();
            switch (v.getId()) {
                case R.id.btn_bianhua:
                    Note notes = new Note(fileName, fileText, ShowActivity.this.note.getTime());
                    notes.setId(note.getId());
                    new NoteController(ShowActivity.this).init().update(note, fileName, fileText);
                    update();
                    break;
                case R.id.btn_dchu:
                    String f1 = "标题内容：" + fileName + "\n" + "正文内容：" + fileText;
                    String s1 = fileName + ".txt";
                    WriteDataToStorage(f1, TestFilePathApkPrivate(getApplicationContext()), s1);
                    new Tools().showToast(ShowActivity.this, "导出到/txt/" + fileName + ".txt文件中!");
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
