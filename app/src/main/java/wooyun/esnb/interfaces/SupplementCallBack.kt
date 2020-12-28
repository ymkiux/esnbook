package wooyun.esnb.interfaces

import android.widget.TextView

interface SupplementCallBack {
    //避免点击回调与显示回调无法区分
    fun onCall(textView: TextView?=null, position: Int? = null)
}