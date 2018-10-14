package com.tpgrade.tpgrade.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tpgrade.tpgrade.R;

import static android.graphics.Color.BLACK;


public class ListNumber extends ListMini {

    int number;
    int buffer;

    public ListNumber(ViewGroup vg, int number) {
        this(vg, number, 0);
    }

    public ListNumber(ViewGroup vg, int number, int buffer) {
        super(vg);
        this.number = number;
        this.buffer = buffer;
    }

    @Override
    public VH createItem(int i, LayoutInflater inflater) {
        return new VH(inflater.inflate(R.layout.item_circle, null));
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void update(int i) {
        TextView v = (TextView) getMiniVH(i).item.findViewById(R.id.tv);
        v.setBackgroundResource(R.drawable.bg_circle_round);
        v.setTextColor(BLACK);
        v.setText("" + (i + buffer));
    }
}
