package zut.edu.cn.notepad.application;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    public App() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "ae25ecbd8b", false);
    }
}
