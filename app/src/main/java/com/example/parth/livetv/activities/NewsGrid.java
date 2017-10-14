package com.example.parth.livetv.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parth.livetv.ChannelImageAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.adapter.ChannelListAdapter;
import com.example.parth.livetv.adapter.VerticalSpaceItemDecorator;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.listners.RecyclerItemClickListener;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.util.ArrayList;

public class NewsGrid extends AppCompatActivity {

    private static final int VERTICAL_ITEM_SPACE = 42;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ChannelListAdapter adapter;
    TextView toolbar_label;
    String newsChannelVideoId[];
    ArrayList<ChannelImageAndLabel> channelImageAndLabels;
    SwipeBackLayout swipeBackLayout;
    ImageView swipe_right_image;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setAllowEnterTransitionOverlap(false);
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setReenterTransition(fade);
        }
        // Get the layout from video_main.xml
        setContentView(R.layout.activity_grid);

        init();

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_label = (TextView) toolbar.findViewById(R.id.label);
        setSupportActionBar(toolbar);
        toolbar_label.setText("Live News");
        channelImageAndLabels = new ArrayList<>();
        addDataToList();
        recyclerView = (RecyclerView) findViewById(R.id.channel_list);
//        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(VERTICAL_ITEM_SPACE));
        swipeBackLayout = (SwipeBackLayout) findViewById(R.id.swipeBackLayout);
        swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        swipe_right_image = (ImageView) findViewById(R.id.swipe_right_image);
        swipe_right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe_right_image.setVisibility(View.GONE);
            }
        });
        if (Constants.newsFirstVisit) {
            showSwipeRightImage();
            Constants.newsFirstVisit = false;
        }
        adapter = new ChannelListAdapter(channelImageAndLabels, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(VERTICAL_ITEM_SPACE));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(NewsGrid.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Intent intent = new Intent(NewsGrid.this, NewsVideoPlayingActivity.class);
                        intent.putExtra("videoID", newsChannelVideoId[position]);
                        intent.putExtra("imageAndLabel", channelImageAndLabels.get(position));
                        intent.putExtra("VideoType", Constants.VideoType.VIDEO);
//                        startActivity(intent);

                        if (Build.VERSION.SDK_INT >= 21) {
                            View logo = view.findViewById(R.id.logo);
                            logo.setTransitionName("toolbar_transition_image");
                            View label = view.findViewById(R.id.label);
                            label.setTransitionName("toolbar_transition_label");
                            Pair<View, String> pair1 = Pair.create(logo, logo.getTransitionName());
                            Pair<View, String> pair2 = Pair.create(label, label.getTransitionName());

                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(NewsGrid.this, pair1, pair2);
                            startActivity(intent, optionsCompat.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }

    public void showSwipeRightImage() {
        swipe_right_image.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe_right_image.setVisibility(View.GONE);

                    }
                });
            }
        };
        thread.start(); //start the thread
    }

    private void addDataToList() {
        newsChannelVideoId = new String[Constants.NEWS_CHANNELS_AND_VIDEO_ID.length];
        for (int i = 0; i < Constants.NEWS_CHANNELS_AND_VIDEO_ID.length; i++) {

            ChannelImageAndLabel channelImageAndLabel = new ChannelImageAndLabel();
            String string[] = Constants.NEWS_CHANNELS_AND_VIDEO_ID[i].split(":", 2);
            channelImageAndLabel.channelName = string[0];
            newsChannelVideoId[i] = string[1];

            int logo = getResources().getIdentifier(getPackageName() + ":drawable/" + "news" + i, null, null);
            channelImageAndLabel.logo = logo;

            channelImageAndLabels.add(channelImageAndLabel);


        }
    }


}
