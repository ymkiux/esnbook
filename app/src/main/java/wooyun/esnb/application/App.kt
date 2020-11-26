package wooyun.esnb.application

import android.app.Application
import com.github.tools.presenter.DataManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
    }
}