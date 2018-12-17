package com.tpgrade.tpgrade.Fragments.ContestKey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tpgrade.tpgrade.List.ListNumber;
import com.tpgrade.tpgrade.List.ListSelectNumber;
import com.tpgrade.tpgrade.R;


public class KeyFragment extends Fragment {

    public static final String ARG_MA_DE = "MaDe";

    ListSelectNumber[] lsNumbrer;

    public KeyFragment() {
    }

    public static KeyFragment create(String made) {
        KeyFragment KeyFragment = new KeyFragment();
        Bundle arg = new Bundle();
        if (made != null) arg.putString(ARG_MA_DE, made);
        KeyFragment.setArguments(arg);
        return KeyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.screen_key, container, false);
        LinearLayout[] lls = {
                (LinearLayout) v.findViewById(R.id.ll2),
                (LinearLayout) v.findViewById(R.id.ll3),
                (LinearLayout) v.findViewById(R.id.ll4),
                (LinearLayout) v.findViewById(R.id.ll5)
        };

        String made = getArguments().getString(ARG_MA_DE);
        int[] selected = new int[]{-1, -1, -1, -1};
        if (made != null && made.length() == 4) {
            try {
                for (int i = 0; i < selected.length; i++)
                    selected[i] = Integer.parseInt(String.valueOf(made.charAt(i)));
            } catch (Exception e) {
            }
        }
        int number = 4;
        new ListNumber((ViewGroup) v.findViewById(R.id.ll1), number, 1).create();
        lsNumbrer = new ListSelectNumber[4];
        for (int i = 0; i < lsNumbrer.length; i++) {
            lsNumbrer[i] = new ListSelectNumber(lls[i], number, selected[i]);
            lsNumbrer[i].create();
        }
        return v;
    }

    public String getMaDe() {
        String a = "";
        for (int i = 0; i < lsNumbrer.length; i++) {
            a += lsNumbrer[i].getSelected() + 1;
        }
        if (a.contains("-1")) return null;
        return a;
    }
}
