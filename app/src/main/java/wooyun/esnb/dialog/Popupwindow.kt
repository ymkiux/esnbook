package wooyun.esnb.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import wooyun.esnb.R

class Popupwindow @SuppressLint("InflateParams", "ClickableViewAccessibility") constructor(context: Activity, itemsOnClick: View.OnClickListener?) : PopupWindow(context) {
    private val mMenuView: View

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMenuView = inflater.inflate(R.layout.alert_show, null)
        val btn_bianhua = mMenuView.findViewById<View>(R.id.btn_bianhua) as TextView
        val btn_dchu = mMenuView.findViewById<View>(R.id.btn_dchu) as TextView

        //设置按钮监听
        btn_dchu.setOnClickListener(itemsOnClick)
        btn_bianhua.setOnClickListener(itemsOnClick)
        //设置SelectPicPopupWindow的View
        this.contentView = mMenuView
        //设置SelectPicPopupWindow弹出窗体的宽
        this.width = ViewGroup.LayoutParams.FILL_PARENT
        //设置SelectPicPopupWindow弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        //设置SelectPicPopupWindow弹出窗体可点击
        this.isFocusable = true
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.animationStyle = R.style.AnimBottom
        //实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(-0x50000000)
        //设置SelectPicPopupWindow弹出窗体的背景
        setBackgroundDrawable(dw)
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener { v: View?, event: MotionEvent ->
            val height = mMenuView.findViewById<View>(R.id.pop_layout).top
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }
    }
}

