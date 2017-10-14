package com.example.parth.livetv.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parth.livetv.ChannelImageAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.adapter.ListAdapter;
import com.example.parth.livetv.adapter.VerticalListItemDecorator;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.listners.RecyclerItemClickListener;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.util.ArrayList;

import static com.example.parth.livetv.R.string.app_name;

public class Dashboard extends AppCompatActivity {

    private static final int VERTICAL_ITEM_SPACE = 21;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ListAdapter adapter;
    TextView toolbar_label;
    ArrayList<ChannelImageAndLabel> channelImageAndLabels;
    SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setAllowEnterTransitionOverlap(true);
            Fade fade = new Fade();
            fade.setDuration(1000);
//            getWindow().setReenterTransition(fade);
        }
        // Get the layout from video_main.xml
        setContentView(R.layout.activity_grid);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar_label = (TextView) toolbar.findViewById(R.id.label);
        toolbar_label.setText(getResources().getString(app_name));

        init();

    }

    private void init() {
        channelImageAndLabels = new ArrayList<>();
        addDataToList();
        recyclerView = (RecyclerView) findViewById(R.id.channel_list);

        adapter = new ListAdapter(channelImageAndLabels, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalListItemDecorator(VERTICAL_ITEM_SPACE));

        swipeBackLayout = (SwipeBackLayout) findViewById(R.id.swipeBackLayout);
        swipeBackLayout.setEnablePullToBack(false);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(Dashboard.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent;
                        switch (position) {
                            case 0:
                                intent = new Intent(Dashboard.this, NewsGrid.class);
                                callActivity(intent, view);
                                break;
                            case 1:
                                intent = new Intent(Dashboard.this, TVChannelsGrid.class);
                                callActivity(intent, view);
                                break;
                            case 2:
                                intent = new Intent(Dashboard.this, MusicGrid.class);
                                callActivity(intent, view);
                                break;
                            default:
                                Toast.makeText(Dashboard.this, "clicked " + position, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

    }

    private void callActivity(Intent intent, View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            View background = view.findViewById(R.id.label);
            background.setTransitionName("toolbar_transition0");
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(Dashboard.this, background, background.getTransitionName());
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void addDataToList() {

        for (int i = 0; i < Constants.DASHBOARD_ITEM_NAMES.length; i++) {

            ChannelImageAndLabel channelImageAndLabel = new ChannelImageAndLabel();
            channelImageAndLabel.channelName = Constants.DASHBOARD_ITEM_NAMES[i];
            int logo = getResources().getIdentifier(getPackageName() + ":drawable/" + "dashboard" + i, null, null);
            channelImageAndLabel.logo = logo;
            channelImageAndLabels.add(channelImageAndLabel);
        }
    }
}
