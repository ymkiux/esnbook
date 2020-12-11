package wooyun.esnb.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View

class RoundButton(context: Context) : View(context) {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var width: Float? = null
    private var hight: Float? = null


    fun setKey(width: Float, hight: Float) {
        this.width = width
        this.hight = hight
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val left = left.toFloat()
        val right = right.toFloat()
        val top = top.toFloat()
        val bottom = bottom.toFloat()
        canvas!!.drawRoundRect(left, top, right, bottom, width!!, hight!!, paint)
    }
}