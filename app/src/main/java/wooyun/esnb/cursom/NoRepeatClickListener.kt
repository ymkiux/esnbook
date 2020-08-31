package wooyun.esnb.cursom

import android.view.View

abstract class NoRepeatClickListener : View.OnClickListener {
    // 最后一次点击的时间
    private var lastClickTime: Long = 0
    override fun onClick(v: View) { // 限制多次点击
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) { // 两次点击的时间间隔大于最小限制时间，则触发点击事件
            lastClickTime = currentTime
            onRepeatClick(v)
        }
    }

    /**
     * 点击事件(相当于@link{android.view.View.OnClickListener})
     *
     * @param v 使用该限制点击的View
     */
    abstract fun onRepeatClick(v: View?)

    companion object {
        // 两次点击按钮之间的最小点击间隔时间(单位:ms)
        private const val MIN_CLICK_DELAY_TIME = 3000
    }
}
