package com.tpgrade.tpgrade.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tpgrade.tpgrade.R;


public class ListAnswer extends ListMini {

    String[] answers;
    Answer1[] Answer1s;

    public ListAnswer(ViewGroup vg, String[] answers) {
        super(vg);
        this.answers = answers;
        Answer1s = new Answer1[answers.length];
    }

    @Override
    public VH createItem(int i, LayoutInflater inflater) {
        return new VH(inflater.inflate(R.layout.item_list_answer, null));
    }

    @Override
    public int getNumber() {
        return answers.length;
    }

    @Override
    public void update(int i) {
        LinearLayout ll = (LinearLayout) getMiniVH(i).item;
        Answer1s[i] = new Answer1(ll, answers[i]);
        Answer1s[i].create();
    }

    public String[] getListAnswers() {
        String[] all = new String[Answer1s.length];
        for (int i = 0; i < Answer1s.length; i++) all[i] = Answer1s[i].getAnswer();
        return all;
    }
}
