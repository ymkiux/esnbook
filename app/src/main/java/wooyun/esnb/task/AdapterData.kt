package wooyun.esnb.task

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import wooyun.esnb.adapter.KyAdapter
import wooyun.esnb.bean.Ky

class AdapterData(private val context: Context) {
    //加载Ky页adapter数据
    fun initKyData(recyclerView: RecyclerView) {
        val list: MutableList<Ky>? = ArrayList<Ky>()
        list?.add(Ky("tools", "tools is an open source library by me, adapted to Android", "https://github.com/ymkiux/tools"))
        list?.add(Ky("Room","Room is a database object mapping library that can easily access the database on the Android application","https://developer.android.google.cn/training/data-storage/room?hl=zh-cn"))
        list?.add(Ky("BaseAdapter","BaseAdapter is a more general adapter library, with as ide template, which can be quickly connected to development","https://github.com/ymkiux/BaseAdapter"))
        val layoutManager = LinearLayoutManager((context as FragmentActivity))
        recyclerView.layoutManager = layoutManager
        val kyAdapter = KyAdapter((context as FragmentActivity))
        kyAdapter.setData(list)
        recyclerView.adapter = kyAdapter
    }
}