package com.tpgrade.tpgrade;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.Adapters.TopicAdapter;
import com.tpgrade.tpgrade.Fragments.Home.CreateNewDialogFragment;
import com.tpgrade.tpgrade.Fragments.Home.EditTopicDialogFragment;
import com.tpgrade.tpgrade.Fragments.Permission.RequestPermissionFragment;

import java.util.List;

import de.cketti.mailto.EmailIntentBuilder;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CreateNewDialogFragment.CreateTopicDialogListener,
        EditTopicDialogFragment.EditTopicDialogListener, ActivityInitialInterface {

    RecyclerView lvTopicList;
    TopicAdapter topicAdapter;
    Toolbar toolbar;

    @Override
    public void initial() {
        setUpRecycleView();

        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new CreateNewDialogFragment();
                dialog.show(getFragmentManager(), "CreateNewDialogFragment");

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction().add(new RequestPermissionFragment(), "RequestPermissionFragment").commit();
    }

    private void setUpRecycleView() {
        lvTopicList = (RecyclerView) findViewById(R.id.lv_topic_list);
        lvTopicList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<Topic> arrayOfTopics = Topic.listAll(Topic.class, "created DESC");
        topicAdapter = new TopicAdapter(arrayOfTopics);
        topicAdapter.setContext(this);
        lvTopicList.setAdapter(topicAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove_all) {
            Topic.deleteAll(Topic.class);
            topicAdapter.clear();
            topicAdapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            this.openSetting();
        } else if (id == R.id.nav_answer_sheet) {

        } else if (id == R.id.nav_guide) {

        } else if (id == R.id.nav_about) {
            this.openAboutUs();
        } else if (id == R.id.nav_share) {
            this.share();
        } else if (id == R.id.nav_send) {
            this.contact();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void contact() {
        Intent emailIntent = EmailIntentBuilder.from(this)
                .to(getString(R.string.home_contact__email))
                .subject(getString(R.string.home_contact__subject))
                .body(getString(R.string.home_contact__body))
                .build();
        startActivity(emailIntent);
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.home_share__message));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void openSetting() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openAboutUs() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFinishCreateTopicDialog(Topic topic) {
        topicAdapter.insert(topic, 0);
        topicAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinishEditTopicDialog(Topic topic) {
        topicAdapter.notifyDataSetChanged();
    }
}
