package com.tpgrade.tpgrade.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpgrade.tpgrade.ContestInfoActivity;
import com.tpgrade.tpgrade.ContestKeyActivity;
import com.tpgrade.tpgrade.ContestKeyCameraActivity;
import com.tpgrade.tpgrade.ContestReviewActivity;
import com.tpgrade.tpgrade.ContestStatisticActivity;
import com.tpgrade.tpgrade.R;

import java.util.List;

public class ContestGridItemAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<ContestItem> listStorage;
    private Context context;

    public ContestGridItemAdapter(Context context, List<ContestItem> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.contest_grid_item, parent, false);
            listViewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            listViewHolder.tvName = (TextView) convertView.findViewById(R.id.tvContestName);
            listViewHolder.cv = (CardView) convertView.findViewById(R.id.cv);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }

        final ContestItem item = listStorage.get(position);
        listViewHolder.ivIcon.setImageDrawable(item.icon);
        listViewHolder.tvName.setText(item.name);
        listViewHolder.key = item.key;

        listViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (item.key) {
                    case "key":
                        intent = new Intent(context, ContestKeyActivity.class);
                        break;
                    case "scan":
                        // intent = new Intent(context, ContestScanActivity.class);
                        intent = new Intent(context, ContestKeyCameraActivity.class);
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

        return convertView;
    }

    static class ViewHolder {
        CardView cv;
        String key;
        TextView tvName;
        ImageView ivIcon;
    }

    public static class ContestItem {
        public Drawable icon;
        public String name;
        public String key;

        public ContestItem(Drawable icon, String name, String key) {
            this.icon = icon;
            this.key = key;
            this.name = name;
        }
    }
}