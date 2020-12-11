package wooyun.esnb.application

import android.app.Application
import com.github.tools.data.Context
//import com.tencent.bugly.crashreport.CrashReport

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Context.init(this)
        //CrashReport.initCrashReport(getApplicationContext(), "ae25ecbd8b", true)
    }

}