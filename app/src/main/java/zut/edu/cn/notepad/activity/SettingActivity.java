package zut.edu.cn.notepad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;

import java.util.Objects;

import zut.edu.cn.notepad.R;
import zut.edu.cn.notepad.cursom.TitleBar;


public class SettingActivity extends AppCompatActivity {

    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_setting);
        init();
        initView();
    }

    private void Intent_Activity() {
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initView() {
        titleBar.setLeftImageResource(R.drawable.ic_arrow_back_black_24dp);
        titleBar.setLeftClickListener(v -> Intent_Activity());


    }

    private void init() {
        titleBar = findViewById(R.id.title_bar);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent_Activity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
