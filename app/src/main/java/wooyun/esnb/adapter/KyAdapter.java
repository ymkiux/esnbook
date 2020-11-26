package wooyun.esnb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wooyun.esnb.R;
import wooyun.esnb.bean.Ky;
import wooyun.esnb.util.Tools;

public class KyAdapter extends RecyclerView.Adapter<KyAdapter.ViewHolder> {

    private List<Ky> kyArrayList = new ArrayList<>();
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recycle_activity_ky_name;
        TextView recycle_activity_ky_info;

        public ViewHolder(View view) {
            super(view);
            recycle_activity_ky_name = (TextView) view.findViewById(R.id.recycle_activity_ky_name);
            recycle_activity_ky_info = (TextView) view.findViewById(R.id.recycle_activity_ky_info);
        }
    }

    public KyAdapter(Context context) {
        this.context=context;
    }

    public void  setData(List<Ky> kyList){
        if (kyArrayList!=null){
            this.kyArrayList.clear();
            this.kyArrayList.addAll(kyList);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_ky_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ky ky = kyArrayList.get(position);
        holder.recycle_activity_ky_name.setText(ky.getName());
        holder.recycle_activity_ky_info.setText(ky.getInfo());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Tools().openBrowser(context,ky.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return kyArrayList.size();
    }

}

