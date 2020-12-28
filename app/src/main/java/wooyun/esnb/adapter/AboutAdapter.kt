package wooyun.esnb.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import wooyun.esnb.R
import wooyun.esnb.base.BaseAdapter
import wooyun.esnb.base.BaseViewHolder
import wooyun.esnb.bean.About
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.interfaces.SupplementCallBack


class AboutAdapter : BaseAdapter<About>(R.layout.fragment_about_adapter) {
    private var supplementCall: SupplementCallBack? = null

    override fun onBindViewHolder(holder: BaseViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val (info, img) = getListData()!![position]
        (holder.getView<ImageView>(R.id.iv_fragment_about_adapter_img))?.setImageResource(img)
        (holder.getView<TextView>(R.id.tv_fragment_about_adapter_info))?.setText(info)
        supplementCall!!.onCall((holder.getView<TextView>(R.id.tv_fragment_about_adapter_info)!!))

        (holder.getView<CardView>(R.id.card__fragment_about_adapter_bg))?.setOnClickListener(object :NoRepeatClickListener(){
            override fun onRepeatClick(v: View?) {
                if (supplementCall != null) {
                    supplementCall!!.onCall((holder.getView<TextView>(R.id.tv_fragment_about_adapter_info)!!),position)
                }
            }
        })
    }

    fun setSupplementCall(supplementCall: SupplementCallBack?) {
        this.supplementCall = supplementCall
    }

}
