package com.example.parth.livetv.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parth.livetv.ChannelImageAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.fragments.YoutubeFragment;

public class NewsVideoPlayingActivity extends AppCompatActivity {
    public static String videoID = "";
    Toolbar toolbar;
    ImageView toolbar_image;
    TextView toolbar_label;
    ChannelImageAndLabel channelImageAndLabel;
    View frameView;
    public static int videoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playing1);

        Intent intent = getIntent();
        videoType = intent.getIntExtra("VideoType", Constants.VideoType.VIDEO);
        channelImageAndLabel = (ChannelImageAndLabel) intent.getSerializableExtra("imageAndLabel");
        videoID = intent.getStringExtra("videoID");

        init();

        setSupportActionBar(toolbar);
        toolbar_image.setImageResource(channelImageAndLabel.logo);
        toolbar_label.setText(channelImageAndLabel.channelName);


        YoutubeFragment fragment = new YoutubeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.activity_video_playing, fragment)
                .commit();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_image = (ImageView) findViewById(R.id.toolbar_image);
        toolbar_label = (TextView) findViewById(R.id.label);
        frameView = findViewById(R.id.activity_video_playing);
    }

    @Override
    public void onBackPressed() {
        YoutubeFragment fragment = (YoutubeFragment) getSupportFragmentManager().findFragmentById(R.id.activity_video_playing);
        if (fragment.exitFullScreen()) {
            fragment.detach();
        }
//        frameView.setVisibility(View.GONE);
        frameView.animate()
                .translationX(frameView.getWidth())
                .alpha(0.0f)
                .setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }

        });

    }
}
