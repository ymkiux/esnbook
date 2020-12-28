package wooyun.esnb.base

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.*

abstract class BaseAdapter<T>(private val getLayoutId: Int) : RecyclerView.Adapter<BaseViewHolder>() {
    private var arrayList: MutableList<T>? = ArrayList()

    @Suppress("UNCHECKED_CAST")
    fun setData(list: List<T?>?) {
        if (arrayList != null) {
            arrayList?.clear()
            arrayList = list as MutableList<T>?
            notifyDataSetChanged()
        }
    }


    fun getListData(): MutableList<T>? {
        return arrayList
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        val view = LayoutInflater.from(p0.context)
                .inflate(getLayoutId, p0, false)
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    fun removeItem(position: Int) {
        arrayList?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun onBindViewHolder(p0: BaseViewHolder, p1: Int) {

    }
}