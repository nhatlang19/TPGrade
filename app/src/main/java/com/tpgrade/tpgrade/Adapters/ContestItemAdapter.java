package com.tpgrade.tpgrade.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpgrade.Lib.DateUtils;
import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.ContestInfoActivity;
import com.tpgrade.tpgrade.ContestKeyActivity;
import com.tpgrade.tpgrade.ContestReviewActivity;
import com.tpgrade.tpgrade.ContestScanActivity;
import com.tpgrade.tpgrade.ContestStatisticActivity;
import com.tpgrade.tpgrade.R;

import java.util.List;

public class ContestItemAdapter extends RecyclerView.Adapter<ContestItemAdapter.ContestItemViewHolder> {

    private Context context;
    private List<ContestItem> items;

    public void setContext(Context context) {
        this.context = context;
    }

    public ContestItemAdapter(List<ContestItem> items) {
        this.items = items;
    }

    @Override
    public ContestItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contest_item, viewGroup, false);
        ContestItemAdapter.ContestItemViewHolder contestItemViewHolder = new ContestItemAdapter.ContestItemViewHolder(v);

        return contestItemViewHolder;
    }

    @Override
    public void onBindViewHolder(ContestItemViewHolder holder, int position) {
        final ContestItem item = items.get(position);
        holder.ivIcon.setImageDrawable(item.icon);
        holder.tvName.setText(item.name);
        holder.key = item.key;

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (item.key) {
                    case "key":
                        intent = new Intent(context, ContestKeyActivity.class);
                        break;
                    case "scan":
                        intent = new Intent(context, ContestScanActivity.class);
                        break;
                    case "review":
                        intent = new Intent(context, ContestReviewActivity.class);
                        break;
                    case "statistic":
                        intent = new Intent(context, ContestStatisticActivity.class);
                        break;
                    case "information":
                        intent = new Intent(context, ContestInfoActivity.class);
                        break;
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ContestItem {
        public Drawable icon;
        public String name;
        public String key;

        public ContestItem() {

        }

        public ContestItem(Drawable icon, String name, String key) {
            this.icon = icon;
            this.key = key;
            this.name = name;
        }
    }

    public static class ContestItemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;

        String key;

        TextView tvName;

        ImageView ivIcon;

        ContestItemViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvName = (TextView) itemView.findViewById(R.id.tvContestName);
            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }
}
