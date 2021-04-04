package wooyun.esnb.task

import android.content.Context
import android.graphics.Bitmap
import android.os.NetworkOnMainThreadException
import android.support.v4.content.AsyncTaskLoader
import wooyun.esnb.api.Api
import wooyun.esnb.bean.FiguresAndSets
import wooyun.esnb.util.BitmapUtil
import wooyun.esnb.util.Tools
import java.io.IOException
import javax.net.ssl.SSLHandshakeException


open class BitmapTaskLoader(context: Context) : AsyncTaskLoader<FiguresAndSets>(context) {
    private var api: String? = null
    private var bitmap: Bitmap? = null
    override fun onStartLoading() {
        forceLoad()
    }

    @Throws(IOException::class, NetworkOnMainThreadException::class, SSLHandshakeException::class)
    override fun loadInBackground(): FiguresAndSets? {
        api = Api.getApi()
        //处理无网络等异常问题
        if (Tools.getNetStatus() != 0) return null
        return api?.let {
            bitmap = BitmapUtil.setBitmap(it)
            FiguresAndSets(bitmap, it)
        }
    }

    override fun onStopLoading() {
        super.onStopLoading()
        cancelLoad()
    }
}