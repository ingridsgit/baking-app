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


public class StepFragment extends Fragment implements Player.EventListener {

    private static final String KEY_STEP = "step";
    private static final String KEY_VIDEO_POSITION = "video_position";
    private static final String KEY_PLAY_WHEN_READY = "play_when_ready";

    private Step currentStep;
    public SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private TextView descriptionView;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if (simpleExoPlayer != null) {
                if (i == AudioManager.AUDIOFOCUS_LOSS || i == AudioManager.
                        AUDIOFOCUS_LOSS_TRANSIENT ||
                        i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    simpleExoPlayer.setPlayWhenReady(false);
                } else if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED && isVisible()) {
                    simpleExoPlayer.setPlayWhenReady(true);
                }
            }
        }
    };
    private AudioFocusRequest.Builder audioFocusRequest;
    private int focus;
    private AudioManager audioManager;
    private boolean isVisible = false;
    private boolean isCreated = false;
    private boolean playWhenReady = true;


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
        audioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_fragment, container, false);
        descriptionView = view.findViewById(R.id.description_text_view);
        playerView = view.findViewById(R.id.player_view);
        if (currentStep != null){
            String uriValue = currentStep.getVideoUrl();

            if (uriValue != null && !uriValue.isEmpty()){
                Uri videoUri = Uri.parse(uriValue);
                setupPlayer(videoUri);
                isCreated = true;
                if (isVisible && isCreated && savedInstanceState == null){
                    requestAudioFocus();
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
        if (Util.SDK_INT <= 23){
            releasePlayer();
        } else if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23){
            releasePlayer();
        }
    }

    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        releasePlayer();
//
//    }

    private void releasePlayer(){
        if (simpleExoPlayer != null){
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        if (mediaSessionCompat != null){
            mediaSessionCompat.setActive(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest.build());
        } else if (audioFocusChangeListener != null){
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_STEP, currentStep);
        if (simpleExoPlayer != null){
            outState.putLong(KEY_VIDEO_POSITION, simpleExoPlayer.getCurrentPosition());
            outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

         if (savedInstanceState != null && simpleExoPlayer != null){
             simpleExoPlayer.seekTo(savedInstanceState.getLong(KEY_VIDEO_POSITION));
             playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
             simpleExoPlayer.setPlayWhenReady(playWhenReady);
             Toast.makeText(getContext(), String.valueOf(playWhenReady), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
//        if (simpleExoPlayer != null && !isVisibleToUser){
//            releasePlayer();
//        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }



    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady){
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, simpleExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == Player.STATE_READY){
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, simpleExoPlayer.getCurrentPosition(), 1f);
        }
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    class CallbackSession extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            super.onPlay();
            simpleExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            simpleExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            simpleExoPlayer.seekTo(0);
        }
    }

    private void setupPlayer(Uri videoUri){
        TrackSelector trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        playerView.setPlayer(simpleExoPlayer);

        String userAgent = Util.getUserAgent(getContext(), "BakingApp");
        DataSource.Factory dataSource = new DefaultDataSourceFactory(getContext(), userAgent);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSource).createMediaSource(videoUri);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.addListener(this);

        mediaSessionCompat = new MediaSessionCompat(getContext(), "StepActivityMediaSession");
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
        mediaSessionCompat.setCallback(new CallbackSession());
        mediaSessionCompat.setActive(true);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    private void requestAudioFocus(){
//        TODO lorque le focus est perdu et que lon reclique sur play, on ne demande plus le focus

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build();
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener);
            focus = audioManager.requestAudioFocus(audioFocusRequest.build());
        } else {
            focus = audioManager.requestAudioFocus(audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            simpleExoPlayer.setPlayWhenReady(true);
        } else {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }


}
