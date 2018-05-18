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
    private static final String KEY_URI_VALUE = "uri_value";
    private static final String KEY_CURRENT_WINDOW = "current_window";

    private Step currentStep;
    private PlayerView playerView;
    private TextView descriptionView;
    private boolean playWhenReady = true;
    private long playerPosition = 0;
    private int currentWindowIndex;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    public SimpleExoPlayer simpleExoPlayer;
    private String uriValue;
    private static AudioFocusRequest.Builder audioFocusRequest;
    private static int focus;
    private static AudioManager audioManager;
    static boolean isFocusGranted = false;
    private boolean isVisible;

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if (simpleExoPlayer != null) {
                if (i == AudioManager.AUDIOFOCUS_LOSS || i == AudioManager.
                        AUDIOFOCUS_LOSS_TRANSIENT ||
                        i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    simpleExoPlayer.setPlayWhenReady(false);
                    isFocusGranted = false;
                } else if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    simpleExoPlayer.setPlayWhenReady(true);
                    isFocusGranted = true;
                }
            }
        }
    };


    public StepFragment(){

    }

    static StepFragment newInstance(Step selectedStep, int position){
        StepFragment fragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_STEP, selectedStep);
        bundle.putInt(KEY_CURRENT_WINDOW, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            currentStep = getArguments().getParcelable(KEY_STEP);
            currentWindowIndex = getArguments().getInt(KEY_CURRENT_WINDOW);
        } else if (savedInstanceState != null){
            currentStep = savedInstanceState.getParcelable(KEY_STEP);
            currentWindowIndex = savedInstanceState.getInt(KEY_CURRENT_WINDOW);
        }
        if (currentStep != null){
            uriValue = currentStep.getVideoUrl();
        }
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_fragment, container, false);
        descriptionView = view.findViewById(R.id.description_text_view);
        playerView = view.findViewById(R.id.player_view);
        if (StepActivity.isLandscape) {
            if (uriValue == null || uriValue.isEmpty()) {
                descriptionView.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
            } else {
                descriptionView.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
            }
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            if (uriValue == null || uriValue.isEmpty()) {
                playerView.setVisibility(View.GONE);
            } else {
                playerView.setVisibility(View.VISIBLE);
            }
        }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                            .build();
                    audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(audioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(audioFocusChangeListener);
                }

            descriptionView.setText(currentStep.getDescription());
        if (currentStep == null){
            playerView.setVisibility(View.GONE);
            descriptionView.setText(R.string.empty_view);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23){
            if (uriValue != null && !uriValue.isEmpty()){
                setupPlayer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23){
            if (uriValue != null && !uriValue.isEmpty()){
                setupPlayer();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23){
            releasePlayer();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23){
                releasePlayer();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
//        if (!isVisibleToUser && simpleExoPlayer != null){
//            simpleExoPlayer.setPlayWhenReady(false);
//        }
        if (isVisibleToUser && simpleExoPlayer != null){
            requestAudioFocus();
            simpleExoPlayer.setPlayWhenReady(playWhenReady && isFocusGranted);
        }
    }

    //        @Override
//    public void onDestroy() {
//        super.onDestroy();
//            ExoPlayerHandler.getExoPlayerHandler().releasePlayer();
//
//    }

    protected void releasePlayer(){
        if (simpleExoPlayer != null){
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        if (mediaSessionCompat != null){
            mediaSessionCompat.setActive(false);
        }
    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_STEP, currentStep);

        if (simpleExoPlayer != null){
            outState.putLong(KEY_VIDEO_POSITION, simpleExoPlayer.getCurrentPosition());
            outState.putBoolean(KEY_PLAY_WHEN_READY, simpleExoPlayer.getPlayWhenReady());
            outState.putInt(KEY_CURRENT_WINDOW, simpleExoPlayer.getCurrentWindowIndex());
            outState.putString(KEY_URI_VALUE, uriValue);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
         if (savedInstanceState != null){
             playerPosition = savedInstanceState.getLong(KEY_VIDEO_POSITION);
             playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
             uriValue = savedInstanceState.getString(KEY_URI_VALUE);
             currentWindowIndex = savedInstanceState.getInt(KEY_CURRENT_WINDOW);
        }
    }



    public void setupPlayer(){
        if (simpleExoPlayer == null) {
            Uri videoUri = Uri.parse(uriValue);
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
        }

//        if (isVisible){
//            Toast.makeText(getContext(), "getuservisiblehint", Toast.LENGTH_SHORT).show();

            simpleExoPlayer.setPlayWhenReady(isFocusGranted && playWhenReady);
            simpleExoPlayer.seekTo(currentWindowIndex,playerPosition);
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



    protected static void requestAudioFocus(){
        if (!isFocusGranted){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                focus = audioManager.requestAudioFocus(audioFocusRequest.build());
//        } else {
//            focus = audioManager.requestAudioFocus(audioFocusChangeListener,
//                    AudioManager.STREAM_MUSIC,
//                    AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        isFocusGranted = focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    static void abandonAudioFocus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest.build());
            Log.d("STEP FRAGMENT", "ABANDON audio focus");
            isFocusGranted = false;
//        } else if (audioFocusChangeListener != null){
//            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }


}
