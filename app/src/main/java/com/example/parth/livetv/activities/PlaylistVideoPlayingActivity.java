package com.example.parth.livetv.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.parth.livetv.ImageUrlAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.adapter.PlaylistAdapter;
import com.example.parth.livetv.adapter.VerticalListItemDecorator;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.fragments.YoutubeFragment;
import com.example.parth.livetv.listners.EndlessRecyclerViewScrollListener;
import com.example.parth.livetv.listners.RecyclerItemClickListener;
import com.example.parth.livetv.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Parth on 06-11-2016.
 */

public class PlaylistVideoPlayingActivity extends AppCompatActivity {
    private static final int ITEMS_IN_PLAYLIST = 25;
    private static final int VERTICAL_ITEM_SPACE = 42;
    public static String videoID = "";
    Toolbar toolbar;
    ImageUrlAndLabel channelLabelAndJsonUrl;
    View frameView;
    ArrayList<ImageUrlAndLabel> episodeImageAndLabel;
    PlaylistAdapter adapter;
    TextView toolbar_label;
    RecyclerView recyclerView;
    LinearLayout video_and_playlist;
    public static int videoType;
    private EndlessRecyclerViewScrollListener scrollListener;
    int serialPosition;
    YoutubeFragment attachedFragment;
    RecyclerView.LayoutManager layoutManager;
    String videoIDList[];
    String pageToken;
    int pageNo = 0;
    boolean firstAsynCall = true;
    int listItemCount = 0;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_video_playing);

        Intent intent = getIntent();
        videoType = intent.getIntExtra("VideoType", Constants.VideoType.VIDEO);
        channelLabelAndJsonUrl = (ImageUrlAndLabel) intent.getSerializableExtra("imageAndLabel");
        videoID = intent.getStringExtra("videoID");
        serialPosition = intent.getIntExtra("position", 0);
        init();


        YoutubeFragment fragment = new YoutubeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.activity_video_playing, fragment)
                .commit();
        addDataToList();
        YoutubeFragment attachedFragment = (YoutubeFragment) getSupportFragmentManager().findFragmentById(R.id.activity_video_playing);

        layoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new VerticalListItemDecorator(VERTICAL_ITEM_SPACE));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(PlaylistVideoPlayingActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        YoutubeFragment fragment = (YoutubeFragment) getSupportFragmentManager().findFragmentById(R.id.activity_video_playing);
                        fragment.loadVideo(videoIDList[position]);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (!pageToken.equals("")) {

                    String baseUrl = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet"
                            + "&maxResults=" + ITEMS_IN_PLAYLIST + "&key="
                            + Constants.DEVELOPER_KEY
                            + "&playlistId="+videoID
                            + "&pageToken=" + pageToken;;
                    PlaylistDataTask playlistDataTask = new PlaylistDataTask(baseUrl);
                    playlistDataTask.execute();
//                    MusicGrid.MusicImageTask musicImageTask = new PlaylistVideoPlayingActivity().MusicImageTask(url);
//                    musicImageTask.execute();
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

    }


    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_label = (TextView) toolbar.findViewById(R.id.label);
        setSupportActionBar(toolbar);
        toolbar_label.setText(channelLabelAndJsonUrl.channelName);
        video_and_playlist = (LinearLayout) findViewById(R.id.video_and_playlist);
        frameView = findViewById(R.id.activity_video_playing);
        recyclerView = (RecyclerView) findViewById(R.id.playlist_recyclerView);

    }

    @Override
    public void onBackPressed() {
        YoutubeFragment fragment = (YoutubeFragment) getSupportFragmentManager().findFragmentById(R.id.activity_video_playing);
        if (fragment.exitFullScreen()) {
            fragment.detach();
            video_and_playlist.animate()
                    .translationX(frameView.getWidth())
                    .alpha(0.0f)
                    .setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    toolbar.animate()
                            .alpha(0.0f)
                            .setDuration(400);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
            });

        }
    }


    private void addDataToList() {
        episodeImageAndLabel = new ArrayList<>();
        String baseUrl = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet"
                + "&maxResults=" + ITEMS_IN_PLAYLIST + "&key="
                + Constants.DEVELOPER_KEY
                + "&playlistId=";
        PlaylistDataTask playlistDataTask = new PlaylistDataTask(baseUrl + videoID);
        playlistDataTask.execute();
    }

    public class PlaylistDataTask extends AsyncTask<Void, Void, ImageUrlAndLabel[]> {
        String jsonUrl;

        public PlaylistDataTask(String jsonUrl) {
            this.jsonUrl = jsonUrl;
        }

        @Override
        protected void onPreExecute() {
            if (firstAsynCall) {
                adapter = new PlaylistAdapter(episodeImageAndLabel, PlaylistVideoPlayingActivity.this);
                recyclerView.setAdapter(adapter);
            }

        }

        @Override
        protected ImageUrlAndLabel[] doInBackground(Void... params) {
            String result = Utils.getMethod(jsonUrl);

            return parseJson(result);

        }

        @Override
        protected void onPostExecute(ImageUrlAndLabel[] imageUrlandEpisodeName) {

            for (int i = 0; i < imageUrlandEpisodeName.length; i++) {
                episodeImageAndLabel.add(imageUrlandEpisodeName[i]);
                adapter.notifyDataSetChanged();
            }
            if (firstAsynCall) {
                firstAsynCall = false;
            }
        }


        private ImageUrlAndLabel[] parseJson(String result) {

            try {
                JSONObject root = new JSONObject(result);
                int totalResults = root.getJSONObject("pageInfo").getInt("totalResults");
                size = Math.min(Math.max(0, totalResults - pageNo * ITEMS_IN_PLAYLIST), ITEMS_IN_PLAYLIST);
                if (firstAsynCall) {
                    videoIDList = new String[totalResults];
                }
                ImageUrlAndLabel imageUrlAndLabel[] = new ImageUrlAndLabel[size];
//                videoIDList = new String[size];
                pageNo++;
                JSONArray array = root.getJSONArray("items");
                if (root.has("nextPageToken")) {
                    pageToken = root.getString("nextPageToken");
                }
                for (int i = 0; i < size; i++) {
                    imageUrlAndLabel[i] = new ImageUrlAndLabel();
                    JSONObject snippet = array.getJSONObject(i)
                            .getJSONObject("snippet");
                    String string[] = snippet.getString("title").split("-", 3);
                    if (string.length > 2) {
                        imageUrlAndLabel[i].channelName = string[0] + string[2];
                    } else {
                        imageUrlAndLabel[i].channelName = string[0];
                    }
                    imageUrlAndLabel[i].url = snippet.getJSONObject("thumbnails")
                            .getJSONObject("medium").getString("url");
                    videoIDList[listItemCount] = snippet.getJSONObject("resourceId")
                            .getString("videoId");
                    listItemCount++;
                }
                return imageUrlAndLabel;
            } catch (
                    JSONException e
                    )

            {
                e.printStackTrace();
                return null;
            }


        }
    }
}

