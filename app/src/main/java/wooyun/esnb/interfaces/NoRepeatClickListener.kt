package wooyun.esnb.interfaces

import android.view.View

abstract class NoRepeatClickListener : View.OnClickListener {
    //最后一次点击的时间
    private var lastClickTime: Long = 0
    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onRepeatClick(v)
        }
    }

    abstract fun onRepeatClick(v: View?)

    companion object {
        // 两次点击按钮之间的最小点击间隔时间(单位:ms)
        private const val MIN_CLICK_DELAY_TIME = 3000
    }
}
