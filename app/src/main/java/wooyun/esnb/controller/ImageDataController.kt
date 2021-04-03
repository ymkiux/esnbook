package wooyun.esnb.controller

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.StrictMode
import android.support.v4.app.FragmentActivity
import android.widget.ImageView
import com.github.tools.interfaces.HandlePostBack
import com.github.tools.operating.L
import com.github.tools.task.ToolsTask
import com.github.tools.task.ToolsTask.saveImageBitmap
import wooyun.esnb.R
import wooyun.esnb.activity.MActivity
import wooyun.esnb.bean.Bitmaps
import wooyun.esnb.util.BitmapUtil

class ImageDataController(private val context: Context) {

    //跳转至详情页
    fun loadImagePage(img: ImageView) {
        val bitmaps: Bitmap? = try {
            (img.drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            null
        }
        when (bitmaps) {
            null -> L.t(context.getString(R.string.wait_a_moment))
            else -> {
                Bitmaps.bitmaps = bitmaps
                val intent = Intent(context, MActivity::class.java)
                intent.putExtra("Fragment", 1)
                context.startActivity(intent)
                (context as FragmentActivity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                com.github.tools.presenter.Tools.handlerPostDelayed(object : HandlePostBack {
                    /** callback interface allows delayed operations to be implemented **/
                    override fun doWork() {
                        context.finish()
                    }
                }, 500)
            }
        }
    }

    //通过url加载显示图片
    fun loadImage(img: ImageView, imageUrl: String) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        val bitmap = BitmapUtil.getBitmap(imageUrl)
        bitmap?.let {
            img.setImageBitmap(it)
        }
    }

    //保存图片至本地
    fun saveImage(img: ImageView) {
        val bitmaps: Bitmap? = try {
            (img.drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            null
        }
        when (bitmaps) {
            null -> L.t(context.getString(R.string.wrong))
            else -> {
                saveImageBitmap(bitmaps)
            }
        }
    }

}