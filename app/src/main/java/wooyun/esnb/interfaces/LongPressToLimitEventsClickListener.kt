package wooyun.esnb.interfaces

import android.view.View

abstract class LongPressToLimitEventsClickListener : View.OnLongClickListener {
    abstract fun onLongPressToLimitEventsClick(v: View?)

    override fun onLongClick(p0: View?): Boolean {
        onLongPressToLimitEventsClick(p0)
        return true
    }
}