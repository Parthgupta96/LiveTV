package com.example.parth.livetv.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parth.livetv.ImageUrlAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.SquareImageView;
import com.example.parth.livetv.adapter.ChannelListAdapter;
import com.example.parth.livetv.adapter.VerticalSpaceItemDecorator;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.listners.RecyclerItemClickListener;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.util.ArrayList;

public class TVChannelsGrid extends AppCompatActivity {
    private static final int VERTICAL_ITEM_SPACE = 42;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ChannelListAdapter adapter;
    TextView toolbar_label;
    ArrayList<ImageUrlAndLabel> channelImageUrlAndLabels;
    SwipeBackLayout swipeBackLayout;
    ImageView swipe_right_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
//            getWindow().setReenterTransition(new Fade());
            getWindow().setAllowReturnTransitionOverlap(false);
        }
        setContentView(R.layout.activity_grid);
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_label = (TextView) toolbar.findViewById(R.id.label);
        setSupportActionBar(toolbar);
        toolbar_label.setText("TV Channels");

        channelImageUrlAndLabels = new ArrayList<>();
        addDataToList();
        swipeBackLayout = (SwipeBackLayout) findViewById(R.id.swipeBackLayout);
        swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        swipe_right_image = (ImageView) findViewById(R.id.swipe_right_image);
        swipe_right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe_right_image.setVisibility(View.GONE);
            }
        });
        if (Constants.TVChannelsFirstVisit) {
            showSwipeRightImage();
            Constants.TVChannelsFirstVisit = false;
        }
        recyclerView = (RecyclerView) findViewById(R.id.channel_list);

        adapter = new ChannelListAdapter(channelImageUrlAndLabels, this, 0);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(VERTICAL_ITEM_SPACE));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(TVChannelsGrid.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        SquareImageView image = (SquareImageView) view.findViewById(R.id.logo);
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        Palette p = Palette.from(bitmap).generate();

                        Intent intent = new Intent(TVChannelsGrid.this, TVChannelsSerialsGrid.class);
                        intent.putExtra("imageUrlAndLabel", channelImageUrlAndLabels.get(position));
                        intent.putExtra("position", position);
                        intent.putExtra("vibrant", p.getLightVibrantColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null)));
                        intent.putExtra("dark_vibrant", p.getDarkVibrantColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null)));
                        startActivity(intent);

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

        for (int i = 0; i < Constants.TVCHANNELS.length; i++) {

            ImageUrlAndLabel channelImageAndLabel = new ImageUrlAndLabel();
            String string[] = Constants.TVCHANNELS[i].split(":", 2);
            channelImageAndLabel.channelName = string[0];
            channelImageAndLabel.url = string[1];

            channelImageUrlAndLabels.add(channelImageAndLabel);


        }
    }


}
