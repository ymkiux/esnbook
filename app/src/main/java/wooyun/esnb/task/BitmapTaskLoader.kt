package wooyun.esnb.task

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.github.tools.task.ToolsTask
import wooyun.esnb.api.Api
import wooyun.esnb.bean.FiguresAndSets
import wooyun.esnb.util.tool

open class BitmapTaskLoader(context: Context) : AsyncTaskLoader<FiguresAndSets>(context) {
    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): FiguresAndSets {
        val api = Api.getApi()
        val bitmap = tool.getBitmap(api)
        //这里不能使用全局变量作返回值 因当前为异步操作
        return FiguresAndSets(bitmap, api)
    }

    override fun onStopLoading() {
        super.onStopLoading()
        cancelLoad()
    }
}