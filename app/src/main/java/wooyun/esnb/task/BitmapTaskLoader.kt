package wooyun.esnb.task

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.AsyncTaskLoader
import com.github.tools.task.ToolsTask
import wooyun.esnb.api.Api
import wooyun.esnb.bean.FiguresAndSets
import wooyun.esnb.util.tool.getNetStatus

open class BitmapTaskLoader(context: Context) : AsyncTaskLoader<FiguresAndSets>(context) {
    private var api: String? = null
    private var bitmap: Bitmap? = null
    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): FiguresAndSets? {
        api = Api.getApi()
        //处理无网络等异常问题
        if (getNetStatus() != 0) return null
        bitmap = ToolsTask.getBitmap(api!!)
        //这里不能使用全局变量作返回值 因当前为异步操作
        return FiguresAndSets(bitmap, api!!)
    }


    override fun onStopLoading() {
        super.onStopLoading()
        cancelLoad()
    }
}