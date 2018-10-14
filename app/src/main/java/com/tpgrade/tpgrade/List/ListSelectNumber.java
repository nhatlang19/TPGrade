package com.tpgrade.tpgrade.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tpgrade.tpgrade.R;

public class ListSelectNumber extends ListMini implements View.OnClickListener {

    int number;
    int selected;

    public ListSelectNumber(ViewGroup vg, int number, int selected) {
        super(vg);
        this.number = number;
        if (selected < number)
            this.selected = selected;
        else this.selected = -1;
    }

    @Override
    public VH createItem(int i, LayoutInflater inflater) {
        return new SNumberVH(inflater.inflate(R.layout.item_circle, null));
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void update(int i) {
        if (i < 0 || i >= number) return;
        SNumberVH vh = (SNumberVH) getMiniVH(i);
        TextView tv = vh.tv;
        tv.setId(i);
        tv.setText("" + i);
        if (selected == i) tv.setBackgroundResource(R.drawable.bg_circle_1);
        else tv.setBackgroundResource(R.drawable.bg_circle_2);
        tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int oldSelected = selected;
        update(selected = v.getId());
        update(oldSelected);
    }

    public int getSelected() {
        return selected;
    }

    public class SNumberVH extends VH {
        TextView tv;

        public SNumberVH(View v) {
            super(v);
            tv = (TextView) v.findViewById(R.id.tv);
        }
    }
}
