package com.tpgrade.tpgrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tpgrade.contants.ContantContest;

public class ContestReviewActivity extends AppCompatActivity {
    private long currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_review);

        currentTopicId = getIntent().getLongExtra(ContantContest.CONTEST_KEY__TOPIC_ID, 0);
    }
}
