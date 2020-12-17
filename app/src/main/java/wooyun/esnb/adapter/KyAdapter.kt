package wooyun.esnb.adapter

import android.content.Context
import android.widget.TextView
import wooyun.esnb.R
import wooyun.esnb.base.BaseAdapter
import wooyun.esnb.base.BaseViewHolder
import wooyun.esnb.bean.Ky
import wooyun.esnb.util.Tools


class KyAdapter(private val context: Context) : BaseAdapter<Ky>(R.layout.activity_ky_adapter) {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val ky: Ky = getListData()!![position]
        (holder.getView<TextView>(R.id.recycle_activity_ky_name))?.text = ky.name
        (holder.getView<TextView>(R.id.recycle_activity_ky_info))?.text = ky.info
        holder.itemView.setOnClickListener { Tools().openBrowser(context, ky.url) }
    }
}

