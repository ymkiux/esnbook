package wooyun.esnb.util

import android.content.Context


object DensityUtils {
    fun dip2px(contex: Context, dp: Int): Int {
        val density = contex.resources.displayMetrics.density
        return (dp * density + 0.5).toInt()
    }

    fun dip2pxT(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}
