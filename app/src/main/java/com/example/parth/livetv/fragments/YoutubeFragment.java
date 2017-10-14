package com.example.parth.livetv.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.parth.livetv.R;
import com.example.parth.livetv.activities.NewsVideoPlayingActivity;
import com.example.parth.livetv.activities.PlaylistVideoPlayingActivity;
import com.example.parth.livetv.constants.Constants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


/**
 * A simple {@link Fragment} subclass.
 */

public class YoutubeFragment extends Fragment {
    YouTubePlayer youTubePlayer;
    private static String VIDEO_ID;
    View videoView;
    int videoType;
    Button fullscreenButton;
    Boolean isFullScreen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);
        videoView = rootView.findViewById(R.id.youtube_layout);
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        fullscreenButton = (Button) rootView.findViewById(R.id.fullscreen_button);
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayer.setFullscreen(true);
                Constants.isFullScreen = true;
            }
        });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_left,
                R.anim.slide_right);
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    if (videoType == Constants.VideoType.VIDEO) {
                        youTubePlayer = player;
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                        player.loadVideo(VIDEO_ID);
                        player.setShowFullscreenButton(true);
//                        player.full
                        player.play();
                    } else if (videoType == Constants.VideoType.PLAYLIST) {
                        youTubePlayer = player;
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                        player.loadPlaylist(VIDEO_ID);
                        player.play();
                    }
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                // YouTube error
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                Log.d("errorMessage:", errorMessage);
            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewsVideoPlayingActivity) {
            this.videoType = NewsVideoPlayingActivity.videoType;
            VIDEO_ID = NewsVideoPlayingActivity.videoID;
        } else if (context instanceof PlaylistVideoPlayingActivity) {
            this.videoType = PlaylistVideoPlayingActivity.videoType;
            VIDEO_ID = PlaylistVideoPlayingActivity.videoID;

        }


    }

    public void detach() {
        if (youTubePlayer != null) {
            youTubePlayer.release();
        }
    }

    public void loadVideo(String id) {
        youTubePlayer.loadVideo(id);
        youTubePlayer.play();
    }

    public boolean exitFullScreen() {
        if (Constants.isFullScreen) {
            youTubePlayer.setFullscreen(false);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Constants.isFullScreen = false;
            return false;
        }else {
            return true;
        }
    }

    public void animateRight() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_right);
        videoView.startAnimation(animation);
    }

}