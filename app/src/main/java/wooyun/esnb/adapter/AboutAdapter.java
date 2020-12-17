package wooyun.esnb.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import wooyun.esnb.R;
import wooyun.esnb.bean.About;
import wooyun.esnb.interfaces.NoRepeatClickListener;
import wooyun.esnb.interfaces.OnCallBack;
import wooyun.esnb.interfaces.SupplementCallBack;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    
    private List<About> aboutArrayList = new ArrayList<>();
    private Context context;
    private OnCallBack onClickCall;
    private SupplementCallBack supplementCall;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView info;
        TextView supplement;
        CardView bg;

        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.iv_fragment_about_adapter_img);
            info = (TextView) view.findViewById(R.id.tv_fragment_about_adapter_info);
            bg = (CardView) view.findViewById(R.id.card__fragment_about_adapter_bg);
            supplement = (TextView) view.findViewById(R.id.tv_fragment_about_adapter_supplement);
        }
    }

    public AboutAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<About> abouts) {
        if (aboutArrayList != null) {
            this.aboutArrayList.clear();
            this.aboutArrayList.addAll(abouts);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_about_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        About about = aboutArrayList.get(position);
        holder.img.setImageResource(about.getImg());
        holder.info.setText(about.getInfo());
        supplementCall.onCall(position, holder.supplement);
        holder.bg.setOnClickListener(new NoRepeatClickListener() {
            @Override
            public void onRepeatClick(@Nullable View v) {
                if (onClickCall != null) {
                    onClickCall.onClick(position, holder.supplement);
                }
            }
        });
        if (position == 6) holder.supplement.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return aboutArrayList.size();
    }


    public void setOnCall(OnCallBack onClickCall) {
        this.onClickCall = onClickCall;
    }

    public void setSupplementCall(SupplementCallBack supplementCall) {
        this.supplementCall = supplementCall;
    }


}


