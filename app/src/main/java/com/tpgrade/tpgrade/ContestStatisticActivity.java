package com.tpgrade.tpgrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ContestStatisticActivity extends AppCompatActivity {
    private long currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_statistic);

        GlobalState global = (GlobalState) getApplication();
        currentTopicId = global.getSelectedTopicId();
    }
}
