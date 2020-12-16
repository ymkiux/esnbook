package wooyun.esnb.controller

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import com.github.tools.interfaces.HandlerListener
import com.github.tools.task.HandlerTask
import wooyun.esnb.bean.Bitmaps
import wooyun.esnb.bean.FiguresAndSets
import wooyun.esnb.task.BitmapTaskLoader


@Suppress("DEPRECATION")
class GetBitmapController(private val context: Context) : LoaderManager.LoaderCallbacks<FiguresAndSets> {
    private val ID: Int = 1

    init {
        (context as FragmentActivity).supportLoaderManager.initLoader(ID, null, this)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<FiguresAndSets> {
        return BitmapTaskLoader((context as FragmentActivity))
    }

    override fun onLoadFinished(p0: Loader<FiguresAndSets>, p1: FiguresAndSets?) {
        //无网络时则不进行以下操作
        if (p1 == null) return
        val message = Message()
        message.what = 0
        message.obj = p1
        HandlerTask(object : HandlerListener {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        val mutableList = msg.obj as FiguresAndSets
                        Bitmaps.bitmaps = mutableList.bitmap
                    }
                }
            }
        }).handler.sendMessage(message)
        //执行完相应操作之后便注销掉这个活动 避免二次加载当前方法
        (context as FragmentActivity).supportLoaderManager.destroyLoader(ID)
    }

    override fun onLoaderReset(p0: Loader<FiguresAndSets>) {

    }
}