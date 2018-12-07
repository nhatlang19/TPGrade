package com.tpgrade.tpgrade.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tpgrade.tpgrade.R;

public class Answer1 extends ListMini implements View.OnClickListener {

    public static final String A = "A", B = "B", C = "C", D = "D", NOT = "";

    public static String[] DAP_AN = {A, B, C, D};
    int selected;

    public Answer1(ViewGroup vg, String dapAn) {
        super(vg);
        selected = -1;
        for (int i = 0; i < DAP_AN.length; i++) {
            if (dapAn.equals(DAP_AN[i])) selected = i;
        }
    }

    @Override
    public VH createItem(int i, LayoutInflater inflater) {
        return new SDapAnVH(inflater.inflate(R.layout.item_circle, null));
    }

    @Override
    public int getNumber() {
        return DAP_AN.length;
    }

    @Override
    public void update(int i) {
        if (i < 0 || i >= getNumber()) return;
        SDapAnVH vh = (SDapAnVH) getMiniVH(i);
        TextView tv = vh.tv;
        tv.setId(i);
        tv.setText(DAP_AN[i]);
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

    public String getAnswer() {
        if (selected == -1) return NOT;
        return DAP_AN[selected];
    }

    public class SDapAnVH extends VH {
        TextView tv;

        public SDapAnVH(View v) {
            super(v);
            tv = (TextView) v.findViewById(R.id.tv);
        }
    }
}
