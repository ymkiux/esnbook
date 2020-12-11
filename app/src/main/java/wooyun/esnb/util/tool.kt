package wooyun.esnb.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


object tool {

    @JvmStatic
    open fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.activeNetworkInfo
            if (info != null && info.isConnected) {
                return info.state == NetworkInfo.State.CONNECTED
            }
        }
        return false
    }

    @JvmStatic
     fun getBitmap(url: String): Bitmap? {
        var bm: Bitmap? = null
        try {
            val iconUrl = URL(url)
            val conn: URLConnection = iconUrl.openConnection()
            val length = conn.contentLength
            conn.connect()
            //获得图像的字符流
            val `is`: InputStream = conn.getInputStream()
            val bis = BufferedInputStream(`is`, length)
            bm = BitmapFactory.decodeStream(bis)
            bis.close()
            `is`.close() //关闭流
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bm
    }
}