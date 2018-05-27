package com.tpgrade.tpgrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.Adapters.ContestItemAdapter;
import com.tpgrade.tpgrade.Adapters.TopicAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContestActivity extends AppCompatActivity {

    public static String CONTEST_KEY__TOPIC_ITEM = "topic_item";

    RecyclerView contest;
    ContestItemAdapter contestItemAdapter;

    private Topic currentTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        currentTopic = (Topic) getIntent().getSerializableExtra(CONTEST_KEY__TOPIC_ITEM);

        setUpRecycleView();
    }

    private void setUpRecycleView() {
        contest = (RecyclerView) findViewById(R.id.contest);
        contest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<ContestItemAdapter.ContestItem> arrayData = new ArrayList<>();
        arrayData.add(new ContestItemAdapter.ContestItem(getDrawable(R.drawable.ic_vpn_key_white_24dp), getString(R.string.contest_nav_key), "key") );
        arrayData.add(new ContestItemAdapter.ContestItem(getDrawable(R.drawable.ic_search_white_24dp), getString(R.string.contest_nav_scan), "scan") );
        arrayData.add(new ContestItemAdapter.ContestItem(getDrawable(R.drawable.ic_view_list_white_24dp), getString(R.string.contest_nav_review), "review") );
        arrayData.add(new ContestItemAdapter.ContestItem(getDrawable(R.drawable.ic_equalizer_white_24dp), getString(R.string.contest_nav_statistic), "statistic") );
        arrayData.add(new ContestItemAdapter.ContestItem(getDrawable(R.drawable.ic_info_white_24dp), getString(R.string.contest_nav_info), "information") );


        contestItemAdapter = new ContestItemAdapter(arrayData);
        contestItemAdapter.setContext(this);
        contest.setAdapter(contestItemAdapter);
    }

    public Topic getCurrentTopic() {
        return currentTopic;
    }
}
