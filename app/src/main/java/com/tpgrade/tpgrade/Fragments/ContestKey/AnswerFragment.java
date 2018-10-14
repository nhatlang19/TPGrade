package com.tpgrade.tpgrade.Fragments.ContestKey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpgrade.tpgrade.List.ListAnswer;
import com.tpgrade.tpgrade.List.ListNumber;
import com.tpgrade.tpgrade.R;

public class AnswerFragment extends Fragment {

    public static final String ARG_DAP_AN = "DapAn";
    ListAnswer listAnswer;

    public static AnswerFragment create(String[] dapAns) {
        AnswerFragment AnswerFragment = new AnswerFragment();
        Bundle arg = new Bundle();
        if (dapAns != null) arg.putStringArray(ARG_DAP_AN, dapAns);
        AnswerFragment.setArguments(arg);
        return AnswerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.screen_answer, container, false);
        String[] dapAn = getArguments().getStringArray(ARG_DAP_AN);
        new ListNumber((ViewGroup) v.findViewById(R.id.ll1), dapAn.length, 1).create();
        listAnswer = new ListAnswer((ViewGroup) v.findViewById(R.id.ll2), dapAn);
        listAnswer.create();
        return v;
    }

    public String[] getListDapAn() {
        return listAnswer.getListAnswers();
    }
}
