package wooyun.esnb.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import wooyun.esnb.R
import wooyun.esnb.activity.ShowActivity
import wooyun.esnb.base.BaseAdapter
import wooyun.esnb.base.BaseViewHolder
import wooyun.esnb.interfaces.LongPressToLimitEventsClickListener
import wooyun.esnb.interfaces.NoRepeatClickListener
import wooyun.esnb.room.Note
import wooyun.esnb.room.NoteController
import wooyun.esnb.util.Tools

class MainAdapter(private val context: Context) : BaseAdapter<Note>(R.layout.fragment_main_data_info) {

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(p0: BaseViewHolder, p1: Int) {
        val note = getListData()!![p1]
        ((p0.getView<TextView>(R.id.tv_title)) as TextView).setText(note.title)
        ((p0.getView<TextView>(R.id.tv_content)) as TextView).setText(note.context)
        ((p0.getView<TextView>(R.id.tv_time)) as TextView).setText(Tools.getTime(note.time.toLong()))
        (p0.getView<CardView>(R.id.card_fragment_about_adapter_bg))?.setOnClickListener(object : NoRepeatClickListener() {
            override fun onRepeatClick(v: View?) {
                val bundle = Bundle()
                val intent = Intent((context as Activity), ShowActivity::class.java)
                bundle.putParcelable("noteID",note)
                intent.putExtras(bundle)
                context.startActivity(intent)
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                context.finish()
            }
        })

        (p0.getView<CardView>(R.id.card_fragment_about_adapter_bg))?.setOnLongClickListener(object : LongPressToLimitEventsClickListener() {
            override fun onLongPressToLimitEventsClick(v: View?) {
                //删除操作
                NoteController(context).init().delete(note)
                removeItem(p1)
            }
        })
    }

}