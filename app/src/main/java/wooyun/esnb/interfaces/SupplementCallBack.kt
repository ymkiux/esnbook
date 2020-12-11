package wooyun.esnb.interfaces

import android.widget.TextView

interface SupplementCallBack {
    fun onCall(position: Int, textView: TextView)
}