package com.tpgrade.tpgrade;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tpgrade.models.Exam;
import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.Fragments.ContestKey.AnswerFragment;
import com.tpgrade.tpgrade.Fragments.ContestKey.KeyFragment;
import com.tpgrade.tpgrade.Fragments.ContestKey.SSPAdapter;

import java.util.Arrays;

public class ContestKeyAddActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    public static int REQUEST_CODE__CONTEST_KEY_ADD = 2;

    public static int ADDNEW = 1;
    public static int EDIT = 2;

    int currentAction;
    Exam exam;
    KeyFragment sMaDe;
    AnswerFragment sDapAn;
    ViewPager viewPager;
    TabLayout tabLayout;
    private long currentTopicId;
    private Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_key_add);

        GlobalState global = (GlobalState) getApplication();
        currentTopicId = global.getSelectedTopicId();

        topic = Topic.findById(Topic.class, currentTopicId);

        this.setUpView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contest_key_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.contest_key_menu__save) {
            exam = new Exam(sMaDe.getMaDe(), topic, sDapAn.getListDapAn());
            exam.save();

            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
