package com.tpgrade.tpgrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.tpgrade.contants.ContantContest;
import com.tpgrade.models.Exam;
import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.Adapters.ContestKeyItemAdapter;

import java.util.List;

public class ContestKeyActivity extends AppCompatActivity {

    RecyclerView lvKeyList;
    ContestKeyItemAdapter keyItemAdapter;
    private long currentTopicId;
    private Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_key);

        GlobalState global = (GlobalState) getApplication();
        currentTopicId = global.getSelectedTopicId();

        topic = Topic.findById(Topic.class, currentTopicId);

        this.setUpRecycleView();
    }

    private void setUpRecycleView() {
        lvKeyList = (RecyclerView) findViewById(R.id.lv_key_list);
        lvKeyList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<Exam> arrayOfItems = Exam.find(Exam.class, "topic = ?", String.valueOf(topic.getId()));

        keyItemAdapter = new ContestKeyItemAdapter(arrayOfItems);
        keyItemAdapter.setContext(this);
        lvKeyList.setAdapter(keyItemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contest_key, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.contest_key_menu__camera) {
//            Intent intent = new Intent(this, ContestKeyCameraActivity.class);
//            startActivityForResult(intent, ContantContest.REQUEST_CODE__CAMERA);
//
//            return true;
//        }

        if (id == R.id.contest_key_menu__add) {
            Intent intent = new Intent(this, ContestKeyAddActivity.class);
            startActivityForResult(intent, ContestKeyAddActivity.REQUEST_CODE__CONTEST_KEY_ADD);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == ContantContest.REQUEST_CODE__CAMERA) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }

        // Check which request we're responding to
        if (requestCode == ContestKeyAddActivity.REQUEST_CODE__CONTEST_KEY_ADD) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                keyItemAdapter.notifyDataSetChanged();
                // Do something with the contact here (bigger example below)
            }
        }
    }
}
