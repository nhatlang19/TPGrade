package com.tpgrade.tpgrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tpgrade.contants.ContantContest;
import com.tpgrade.models.Topic;

public class ContestKeyActivity extends AppCompatActivity {
    private long currentTopicId;

    private Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_key);

        currentTopicId = getIntent().getLongExtra(ContantContest.CONTEST_KEY__TOPIC_ID, 0);

        topic = Topic.findById(Topic.class, currentTopicId);
    }
}
