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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import wooyun.esnb.R;
import wooyun.esnb.bean.Values;
import wooyun.esnb.cursom.TitleBar;
import wooyun.esnb.sql.DbOpenHelper;

import static wooyun.esnb.activity.MainActivity.hasKitKat;
import static wooyun.esnb.activity.MainActivity.hasLollipop;

public class EditActivity extends AppCompatActivity {

    DbOpenHelper myDb;
    private Button btnCancel;
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView timeTextView;
    private Values value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//让布局向上移来显示软键盘
        setContentView(R.layout.activity_editor);
        init();

        EditText et = findViewById(R.id.et_title);//设置光标
        et.setCursorVisible(false); /*设置光标不显示*/
        EditText et1 = findViewById(R.id.et_content);
        et1.setSelectAllOnFocus(true);   /*获得焦点时全选文本*/

        if (timeTextView.getText().length() == 0)
            timeTextView.setText(getTime());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);

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

    private void init() {

        myDb = new DbOpenHelper(this);
        SQLiteDatabase db = myDb.getReadableDatabase();
        titleEditText = findViewById(R.id.et_title);
        contentEditText = findViewById(R.id.et_content);
        timeTextView = findViewById(R.id.edit_time);
        FloatingActionButton btnSave = findViewById(R.id.btn_save);


        btnSave.setOnClickListener(v -> {

            SQLiteDatabase db1 = myDb.getWritableDatabase();
            ContentValues values = new ContentValues();

            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();
            String time = timeTextView.getText().toString();

            if ("".equals(titleEditText.getText().toString()) || "".equals(contentEditText.getText().toString())) {
                /* Toasty.warning(EditActivity.this, "标题或者内容不能为空", Toast.LENGTH_SHORT, true).show();
                 */
                Toast.makeText(EditActivity.this, "标题或者内容不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            values.put(DbOpenHelper.TITLE, title);
            values.put(DbOpenHelper.CONTENT, content);
            values.put(DbOpenHelper.TIME, time);
            db1.insert(DbOpenHelper.TABLE, null, values);
            Toast.makeText(EditActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();
            /*Toasty.success(EditActivity.this, "保存成功!", Toast.LENGTH_SHORT, true).show();
             */
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            db1.close();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    //获取当前时间
    private String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str = sdf.format(date);
        return str;
    }

    //物理返回键重写事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            save_dismiss();
        }
        return true;
    }

    private void save_dismiss() {
        SQLiteDatabase db = myDb.getWritableDatabase();
        ContentValues values = new ContentValues();

        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String time = timeTextView.getText().toString();

        if ("".equals(titleEditText.getText().toString()) || "".equals(contentEditText.getText().toString())) {
            startActivity(new Intent(EditActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            Toast.makeText(EditActivity.this, "无内容", Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(DbOpenHelper.TITLE, title);
        values.put(DbOpenHelper.CONTENT, content);
        values.put(DbOpenHelper.TIME, time);
        db.insert(DbOpenHelper.TABLE, null, values);
        Toast.makeText(EditActivity.this, "保存成功.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        db.close();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
