package wooyun.esnb.controller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.widget.ImageView
import wooyun.esnb.bean.FiguresAndSets
import wooyun.esnb.task.BitmapTaskLoader

//当前类必须重写在onViewCreated方法内 避免因为生命周期导致二次加载
@Suppress("DEPRECATION")
class UrlController(private val context: Context, private val iv_fragment_ky_image: ImageView) : LoaderManager.LoaderCallbacks<FiguresAndSets> {
    private val ID: Int = 0
    private var imageUrl: String? = null

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
        handler.sendMessage(message)
        //执行完相应操作之后便注销掉这个活动 避免二次加载当前方法
        (context as FragmentActivity).supportLoaderManager.destroyLoader(ID)
    }

    override fun onLoaderReset(p0: Loader<FiguresAndSets>) {}

    private val handler: Handler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    val mutableList = msg.obj as FiguresAndSets
                    imageUrl = mutableList.api
                    val bitmaps = mutableList.bitmap
                    if (bitmaps != null) {
                        loadImg(bitmaps)
                        return
                    }
                    ImageDataController((context as FragmentActivity)).loadImage(iv_fragment_ky_image, imageUrl!!)
                }
            }
        }
    }

    private fun loadImg(bitmap: Bitmap) {
        iv_fragment_ky_image.setImageBitmap(bitmap)
    }
}