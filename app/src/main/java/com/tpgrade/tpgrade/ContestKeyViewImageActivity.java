package com.tpgrade.tpgrade;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.tpgrade.contants.ContantContest;
import com.tpgrade.tpgrade.Processors.CaptureImage;

public class ContestKeyViewImageActivity extends AppCompatActivity {

    private static int DEPLAY_TIME = 3000;

    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_key_view_image);

        addListener();

        autoClose(DEPLAY_TIME);
    }

    private void addListener() {
        imgView = (ImageView) findViewById(R.id.imgView);
        String pathImage = getIntent().getStringExtra(ContantContest.CONTEST_KEY_VIEW_IMAGE_PATH);
        imgView.setImageDrawable(Drawable.createFromPath(pathImage));
    }

    private void autoClose(int delayMillis) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        CaptureImage.DID_CAPTURE = false;
                        GlobalState.resetRect();
                        ContestKeyViewImageActivity.this.finish();
                    }
                },
                delayMillis);
    }
}
