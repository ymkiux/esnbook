package wooyun.esnb.interfaces

import android.view.View

interface DoubleClickLister : View.OnClickListener {

    override fun onClick(p0: View?) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time < interval) {
            OnDoubleClickLister(p0)
        }
        time = currentTimeMillis
    }

    fun OnDoubleClickLister(v: View?)

    companion object {
        private var interval: Long = 500
        private var time: Long = 0
    }

}