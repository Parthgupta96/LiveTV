package com.example.parth.livetv.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parth.livetv.ChannelImageAndLabel;
import com.example.parth.livetv.ImageUrlAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.adapter.SerialListAdapter;
import com.example.parth.livetv.adapter.VerticalSpaceItemDecorator;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.listners.EndlessRecyclerViewScrollListener;
import com.example.parth.livetv.listners.RecyclerItemClickListener;
import com.example.parth.livetv.util.Utils;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MusicGrid extends AppCompatActivity {
    private static final int ITEMS_IN_PLAYLIST = 50;
    private static final int VERTICAL_ITEM_SPACE = 42;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SerialListAdapter adapter;
    ArrayList<ImageUrlAndLabel> musicImageUrlAndLabel;
    String videoID[];
    TextView toolbar_label;
    View rootView;
    SwipeBackLayout swipeBackLayout;
    ProgressBar progressBar;
    ImageView swipe_right_image;
    private EndlessRecyclerViewScrollListener scrollListener;
    String pageToken;
    int pageNo = 0;
    boolean firstAsynCall = true;
    int listItemCount = 0;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
//            getWindow().setReenterTransition(new Fade());
//            getWindow().setAllowReturnTransitionOverlap(false);
        }
        setContentView(R.layout.activity_grid);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_label = (TextView) toolbar.findViewById(R.id.label);
        setSupportActionBar(toolbar);
        toolbar_label.setText("Music");
        swipe_right_image = (ImageView) findViewById(R.id.swipe_right_image);
        swipe_right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe_right_image.setVisibility(View.GONE);
            }
        });
        musicImageUrlAndLabel = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.channel_list);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        showProgress(1);
        swipeBackLayout = (SwipeBackLayout) findViewById(R.id.swipeBackLayout);
        swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        rootView = findViewById(R.id.activity_main);

        addDataToList();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MusicGrid.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(VERTICAL_ITEM_SPACE));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MusicGrid.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(MusicGrid.this, NewsVideoPlayingActivity.class);
                        intent.putExtra("videoID", videoID[position]);
                        intent.putExtra("VideoType", Constants.VideoType.VIDEO);
                        intent.putExtra("imageAndLabel", new ChannelImageAndLabel("Music"));
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
        scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                if (!pageToken.equals("")) {
                    String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet"
                            + "&maxResults=" + ITEMS_IN_PLAYLIST + "&key="
                            + Constants.DEVELOPER_KEY
                            + "&playlistId=" + Constants.MUSIC_PLALIST_ID
                            + "&pageToken=" + pageToken;

                    MusicImageTask musicImageTask = new MusicImageTask(url);
                    musicImageTask.execute();
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

    }

    private void addDataToList() {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet"
                + "&maxResults=" + ITEMS_IN_PLAYLIST + "&key="
                + Constants.DEVELOPER_KEY
                + "&playlistId=" + Constants.MUSIC_PLALIST_ID;

        MusicImageTask musicImageTask = new MusicImageTask(url);
        musicImageTask.execute();
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

    private void showProgress(int i) {
        if (i == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }
    }

    public class MusicImageTask extends AsyncTask<Void, Void, ImageUrlAndLabel[]> {
        String jsonUrl;

        public MusicImageTask(String jsonUrl) {
            this.jsonUrl = jsonUrl;
        }

        @Override
        protected void onPreExecute() {
            if (firstAsynCall) {
                adapter = new SerialListAdapter(musicImageUrlAndLabel, MusicGrid.this);
                recyclerView.setAdapter(adapter);
            }
        }

        @Override
        protected ImageUrlAndLabel[] doInBackground(Void... params) {
            String result = Utils.getMethod(jsonUrl);

            return parseJson(result);

        }

        @Override
        protected void onPostExecute(ImageUrlAndLabel[] imageUrlAndSongTitle) {
            if (imageUrlAndSongTitle != null) {
                for (int i = 0; i < imageUrlAndSongTitle.length; i++) {
                    musicImageUrlAndLabel.add(imageUrlAndSongTitle[i]);
                    adapter.notifyItemInserted((i + 1) * size);
                }
                if (firstAsynCall) {
                    showProgress(0);
                    if (Constants.musicFirstVisit) {
                        showSwipeRightImage();
                        Constants.musicFirstVisit = false;
                    }
                    firstAsynCall = false;
                }
            } else {
                Toast.makeText(MusicGrid.this, "Music not found", Toast.LENGTH_SHORT).show();
            }

        }

        private ImageUrlAndLabel[] parseJson(String result) {
            try {
                JSONObject root = new JSONObject(result);
                int totalResults = root.getJSONObject("pageInfo").getInt("totalResults");
                size = Math.min(Math.max(0, totalResults - pageNo * ITEMS_IN_PLAYLIST), ITEMS_IN_PLAYLIST);
                if (firstAsynCall) {
                    videoID = new String[totalResults];
                }
                ImageUrlAndLabel imageUrlAndLabel[] = new ImageUrlAndLabel[size];
                JSONArray array = root.getJSONArray("items");
                pageNo++;
                if (root.has("nextPageToken")) {
                    pageToken = root.getString("nextPageToken");
                }
                for (int i = 0; i < size; i++) {

                    imageUrlAndLabel[i] = new ImageUrlAndLabel();
                    JSONObject snippet = array.getJSONObject(i)
                            .getJSONObject("snippet");
                    imageUrlAndLabel[i].channelName = snippet.getString("title");
                    imageUrlAndLabel[i].url = snippet.getJSONObject("thumbnails")
                            .getJSONObject("high").getString("url");
                    videoID[listItemCount] = snippet.getJSONObject("resourceId")
                            .getString("videoId");
                    listItemCount++;
                }
                return imageUrlAndLabel;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


}
