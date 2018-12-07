package com.tpgrade.tpgrade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tpgrade.contants.ContantContest;
import com.tpgrade.tpgrade.Adapters.ContestGridItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContestGridActivity extends AppCompatActivity {

    GridView gvContest;

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_grid);

        GlobalState global = (GlobalState) getApplication();

        if (global.getSelectedTopicId() == 0) {
            long currentTopicId = getIntent().getLongExtra(ContantContest.CONTEST_KEY__TOPIC_ID, 0);
            global.setSelectedTopicId(currentTopicId);
        }

        setupGridView();
    }

    private void setupGridView() {
        gvContest = (GridView) findViewById(R.id.gvContest);

        List<ContestGridItemAdapter.ContestItem> allItems = getAllItemObject();
        ContestGridItemAdapter customAdapter = new ContestGridItemAdapter(ContestGridActivity.this, allItems);
        gvContest.setAdapter(customAdapter);
    }

    private List<ContestGridItemAdapter.ContestItem> getAllItemObject() {
        List<ContestGridItemAdapter.ContestItem> arrayData = new ArrayList<>();
        arrayData.add(new ContestGridItemAdapter.ContestItem(getDrawable(R.drawable.ic_vpn_key_white_24dp), getString(R.string.contest_nav_key), "key"));
        arrayData.add(new ContestGridItemAdapter.ContestItem(getDrawable(R.drawable.ic_search_white_24dp), getString(R.string.contest_nav_scan), "scan"));
//        arrayData.add(new ContestGridItemAdapter.ContestItem(getDrawable(R.drawable.ic_view_list_white_24dp), getString(R.string.contest_nav_review), "review"));
//        arrayData.add(new ContestGridItemAdapter.ContestItem(getDrawable(R.drawable.ic_equalizer_white_24dp), getString(R.string.contest_nav_statistic), "statistic"));
//        arrayData.add(new ContestGridItemAdapter.ContestItem(getDrawable(R.drawable.ic_info_white_24dp), getString(R.string.contest_nav_info), "information"));
        return arrayData;
    }
}
