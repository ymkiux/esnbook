package wooyun.esnb.application

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import com.github.tools.data.Context


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Context.init(this)
        //CrashReport.initCrashReport(getApplicationContext(), "ae25ecbd8b", true)
        val res: Resources = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.getDisplayMetrics())
    }
}