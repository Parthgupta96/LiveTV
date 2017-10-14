package com.example.parth.livetv.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.parth.livetv.ImageUrlAndLabel;
import com.example.parth.livetv.R;
import com.example.parth.livetv.adapter.SerialListAdapter;
import com.example.parth.livetv.adapter.VerticalSpaceItemDecorator;
import com.example.parth.livetv.constants.Constants;
import com.example.parth.livetv.listners.RecyclerItemClickListener;
import com.example.parth.livetv.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.parth.livetv.constants.Constants.TV_CHANNEL_SERIALS;

public class TVChannelsSerialsGrid extends AppCompatActivity {
    private static final int VERTICAL_ITEM_SPACE = 42;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SerialListAdapter adapter;
    ImageUrlAndLabel imageUrlAndLabel;
    ArrayList<ImageUrlAndLabel> serialImageUrlAndLabels;
    int tvChannelPosition;
    View rootView;
    int toolabrColor;
    int statusbarColor;
    String playlistId[];
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setEnterTransition(new Slide(Gravity.LEFT));
            getWindow().setExitTransition(new Slide(Gravity.BOTTOM));
            getWindow().setReturnTransition(new Slide(Gravity.BOTTOM));
        }
        setContentView(R.layout.activity_tvchannels_serails_grid);
        Intent intent = getIntent();
        imageUrlAndLabel = (ImageUrlAndLabel) intent.getSerializableExtra("imageUrlAndLabel");
        tvChannelPosition = intent.getIntExtra("position", 0);
        int defaultColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        int defaultColorDark = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null);
        toolabrColor = intent.getIntExtra("vibrant", defaultColor);
        statusbarColor = intent.getIntExtra("dark_vibrant", defaultColorDark);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(statusbarColor);
        }
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(imageUrlAndLabel.channelName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(toolabrColor));
        serialImageUrlAndLabels = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.channel_list);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        showProgress(1);
        rootView = findViewById(R.id.tvchannels_collapsable_toolbar);
        addDataToList();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(VERTICAL_ITEM_SPACE));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(TVChannelsSerialsGrid.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(TVChannelsSerialsGrid.this, PlaylistVideoPlayingActivity.class);
                        intent.putExtra("imageAndLabel", serialImageUrlAndLabels.get(position));
                        intent.putExtra("videoID", playlistId[position]);
                        intent.putExtra("VideoType", Constants.VideoType.PLAYLIST);
                        intent.putExtra("position",position);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

    }

    private void addDataToList() {
        String jsonUrlArray[] = new String[Constants.TV_CHANNEL_SERIALS[tvChannelPosition].length];
        playlistId = new String[Constants.TV_CHANNEL_SERIALS[tvChannelPosition].length];
        String channelNameArray[] = new String[Constants.TV_CHANNEL_SERIALS[tvChannelPosition].length];
        String baseUrl = "https://www.googleapis.com/youtube/v3/playlists?";

        for (int i = 0; i < TV_CHANNEL_SERIALS[tvChannelPosition].length; i++) {

            String string[] = TV_CHANNEL_SERIALS[tvChannelPosition][i].split(":", 2);

            jsonUrlArray[i] = baseUrl + "id=" + string[1] + "&key=" + Constants.DEVELOPER_KEY + "&part=snippet";
            playlistId[i] = string[1];
            channelNameArray[i] = string[0];

        }
        SerialImageTask playlistDataTask = new SerialImageTask(jsonUrlArray, channelNameArray);
        playlistDataTask.execute();
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

    @Override
    public void onBackPressed() {
        recyclerView.animate()
                .translationX(recyclerView.getWidth())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            finishAfterTransition();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        toolbar.animate()
                                .alpha(0.5f)
                                .setDuration(200);
                        rootView.animate()
                                .alpha(0.5f)
                                .setDuration(200);
                    }
                });
    }

    public class SerialImageTask extends AsyncTask<Void, Void, String[]> {
        ImageUrlAndLabel imageUrlAndLabel;
        String[] channelName;
        String[] jsonUrl;

        public SerialImageTask(String[] jsonUrl, String[] channelName) {
            this.channelName = channelName;
            this.jsonUrl = jsonUrl;
        }

        @Override
        protected void onPreExecute() {
            adapter = new SerialListAdapter(serialImageUrlAndLabels, TVChannelsSerialsGrid.this);
            recyclerView.setAdapter(adapter);

        }

        @Override
        protected String[] doInBackground(Void... params) {
            String result[] = new String[jsonUrl.length];
            for (int i = 0; i < jsonUrl.length; i++) {
                result[i] = Utils.getMethod(jsonUrl[i]);
            }

            return parseJson(result);

        }

        @Override
        protected void onPostExecute(String[] imageUrls) {

            for (int i = 0; i < imageUrls.length; i++) {
                imageUrlAndLabel = new ImageUrlAndLabel();
                imageUrlAndLabel.channelName = channelName[i];
                imageUrlAndLabel.url = imageUrls[i];
                serialImageUrlAndLabels.add(imageUrlAndLabel);
                adapter.notifyDataSetChanged();
            }
            showProgress(0);

        }

        private String[] parseJson(String result[]) {
            String url[] = new String[result.length];
            for (int i = 0; i < result.length; i++) {

                url[i] = "";
                try {
                    JSONObject root = new JSONObject(result[i]);
                    url[i] = root.getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONObject("snippet")
                            .getJSONObject("thumbnails")
                            .getJSONObject("high").getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return url;
        }
    }

}
