package com.example.android.bakingapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class StepFragment extends Fragment  {

    private static final String KEY_STEP = "step";
    private static final String KEY_VIDEO_POSITION = "video_position";
    private static final String KEY_PLAY_WHEN_READY = "play_when_ready";

    private Step currentStep;
    private PlayerView playerView;
    private TextView descriptionView;

    private boolean isVisible = false;
    private boolean isCreated = false;
    private boolean playWhenReady = true;
    private boolean isLandscape = false;


    public StepFragment(){

    }

    static StepFragment newInstance(Step selectedStep){
        StepFragment fragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_STEP, selectedStep);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            currentStep = getArguments().getParcelable(KEY_STEP);
        } else if (savedInstanceState != null){
            currentStep = savedInstanceState.getParcelable(KEY_STEP);
        }
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onResume() {
        super.onResume();
        ExoPlayerHandler.getExoPlayerHandler().goToForeground();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_fragment, container, false);
        descriptionView = view.findViewById(R.id.description_text_view);
        playerView = view.findViewById(R.id.player_view);
        if (StepActivity.isLandscape){
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
        }
        if (currentStep != null){
            String uriValue = currentStep.getVideoUrl();

            if (uriValue != null && !uriValue.isEmpty()){
                Uri videoUri = Uri.parse(uriValue);
                ExoPlayerHandler.getExoPlayerHandler().setupPlayer(videoUri, playerView, getContext());
                isCreated = true;
                if (isVisible && isCreated && savedInstanceState == null){
                    ExoPlayerHandler.getExoPlayerHandler().requestAudioFocus();
                }
            } else {
                playerView.setVisibility(View.GONE);
            }
            descriptionView.setText(currentStep.getDescription());
        } else {
            playerView.setVisibility(View.GONE);
            descriptionView.setText(R.string.empty_view);
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ExoPlayerHandler.getExoPlayerHandler().goToBackground();
//        if (Util.SDK_INT <= 23){
//            releasePlayer();
//        } else if (simpleExoPlayer != null) {
//            playWhenReady = simpleExoPlayer.getPlayWhenReady();
//            simpleExoPlayer.setPlayWhenReady(false);
//        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23){
//            releasePlayer();
//        }
//    }

        @Override
    public void onDestroy() {
        super.onDestroy();
            ExoPlayerHandler.getExoPlayerHandler().releasePlayer();

    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_STEP, currentStep);
        SimpleExoPlayer currentExoPlayer = ExoPlayerHandler.getExoPlayerHandler().getSimpleExoPlayer();
        if (currentExoPlayer != null){
            outState.putLong(KEY_VIDEO_POSITION, currentExoPlayer.getCurrentPosition());
            outState.putBoolean(KEY_PLAY_WHEN_READY, ExoPlayerHandler.getExoPlayerHandler().getPlayWhenReady());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        SimpleExoPlayer currentExoPlayer = ExoPlayerHandler.getExoPlayerHandler().getSimpleExoPlayer();
         if (savedInstanceState != null && currentExoPlayer != null){
             currentExoPlayer.seekTo(savedInstanceState.getLong(KEY_VIDEO_POSITION));
             playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
             currentExoPlayer.setPlayWhenReady(playWhenReady);
//             Toast.makeText(getContext(), String.valueOf(playWhenReady), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (!isVisibleToUser){
            ExoPlayerHandler.getExoPlayerHandler().goToBackground();
        }
    }



}
