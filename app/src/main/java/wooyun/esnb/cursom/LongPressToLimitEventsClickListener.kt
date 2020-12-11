package wooyun.esnb.cursom

import android.view.View

abstract class LongPressToLimitEventsClickListener : View.OnLongClickListener {
    private var status = false
    abstract fun onLongPressToLimitEventsClick(v: View?)

    override fun onLongClick(p0: View?): Boolean {
        if (!status) {
            status = true
            onLongPressToLimitEventsClick(p0)
        }
        return true
    }

    fun setStatus(status:Boolean){
        this.status=status
    }
}