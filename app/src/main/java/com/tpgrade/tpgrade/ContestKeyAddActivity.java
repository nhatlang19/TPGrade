package com.tpgrade.tpgrade;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tpgrade.models.Exam;
import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.Fragments.ContestKey.AnswerFragment;
import com.tpgrade.tpgrade.Fragments.ContestKey.KeyFragment;
import com.tpgrade.tpgrade.Fragments.ContestKey.SSPAdapter;

import java.util.Arrays;

public class ContestKeyAddActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    public static int ADDNEW = 1;
    public static int EDIT = 2;

    int currentAction;

    private long currentTopicId;
    private Topic topic;

    Exam exam;

    KeyFragment sMaDe;
    AnswerFragment sDapAn;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_key_add);

        GlobalState global = (GlobalState) getApplication();
        currentTopicId = global.getSelectedTopicId();

        topic = Topic.findById(Topic.class, currentTopicId);

//        exam = new Exam();
//        exam.topic = topic;

        this.setUpView();
    }

    private void setUpView() {
        viewPager = (ViewPager) findViewById(R.id.vp);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("MÃ ĐỀ"));
        tabLayout.addTab(tabLayout.newTab().setText("ĐÁP ÁN"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        String[] answers = new String[topic.numbers];
        Arrays.fill(answers, "");
        Fragment[] fragments = new Fragment[]{
            sMaDe = KeyFragment.create(null),
            sDapAn = AnswerFragment.create(answers)
        };

        SSPAdapter adapter = new SSPAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View view) {

    }
}
