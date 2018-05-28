package com.tpgrade.tpgrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.tpgrade.Lib.DateUtils;
import com.tpgrade.contants.ContantContest;
import com.tpgrade.models.Topic;

public class ContestInfoActivity extends AppCompatActivity {

    private long currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_info);

        currentTopicId = getIntent().getLongExtra(ContantContest.CONTEST_KEY__TOPIC_ID, 0);

        TextView tvInfoTestName = (TextView) findViewById(R.id.tvInfoTestName);
        TextView tvInfoTypePaper = (TextView) findViewById(R.id.tvInfoTypePaper);
        TextView tvInfoNumbers = (TextView) findViewById(R.id.tvInfoNumbers);
        TextView tvInfoTopScore = (TextView) findViewById(R.id.tvInfoTopScore);
        TextView tvInfoCreated = (TextView) findViewById(R.id.tvInfoCreated);
        TextView tvInfoAnswerNumber = (TextView) findViewById(R.id.tvInfoAnswerNumber);
        TextView tvInfoKeyNumber = (TextView) findViewById(R.id.tvInfoKeyNumber);
        TextView tvInfoAvgScore = (TextView) findViewById(R.id.tvInfoAvgScore);
        TextView tvInfoMinScore = (TextView) findViewById(R.id.tvInfoMinScore);
        TextView tvInfoMaxScore = (TextView) findViewById(R.id.tvInfoMaxScore);

        Topic topic = Topic.findById(Topic.class, 0);

        if (topic != null) {
            tvInfoTestName.setText(topic.testName);
            tvInfoTypePaper.setText(topic.typePaper + "");
            tvInfoNumbers.setText(topic.numbers + "");
            tvInfoTopScore.setText(topic.topScore + "");
            tvInfoCreated.setText(DateUtils.formatCreated(topic.created));
            tvInfoAnswerNumber.setText(topic.answerNumber + "");
            tvInfoKeyNumber.setText(topic.keyNumber + "");
            tvInfoAvgScore.setText(topic.averageScore + "");
            tvInfoMinScore.setText(topic.minScore + "");
            tvInfoMaxScore.setText(topic.maxScore + "");
        } else {
            Toast.makeText(this, getString(R.string.contest_message__error), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }
}
