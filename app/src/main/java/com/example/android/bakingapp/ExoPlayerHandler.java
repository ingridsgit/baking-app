package com.example.android.bakingapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

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

public class ExoPlayerHandler implements Player.EventListener{

    private static ExoPlayerHandler exoPlayerHandler;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    public SimpleExoPlayer simpleExoPlayer;
    private Uri uri;
    private AudioFocusRequest.Builder audioFocusRequest;
    private int focus;
    private AudioManager audioManager;
    private boolean playWhenReady = true;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if (simpleExoPlayer != null) {
                if (i == AudioManager.AUDIOFOCUS_LOSS || i == AudioManager.
                        AUDIOFOCUS_LOSS_TRANSIENT ||
                        i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    simpleExoPlayer.setPlayWhenReady(false);
                } else if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    simpleExoPlayer.setPlayWhenReady(true);
                }
            }
        }
    };


    public static ExoPlayerHandler getExoPlayerHandler() {
        if (exoPlayerHandler == null){
            exoPlayerHandler = new ExoPlayerHandler();
        }
        return exoPlayerHandler;
    }

    public SimpleExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;
    }

    public boolean getPlayWhenReady() {
        return playWhenReady;
    }


    public void setupPlayer(Uri videoUri, PlayerView playerView, Context context){
        if (simpleExoPlayer == null || uri != videoUri){
            audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
            TrackSelector trackSelector = new DefaultTrackSelector();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            playerView.setPlayer(simpleExoPlayer);

            uri = videoUri;
            String userAgent = Util.getUserAgent(context, "BakingApp");
            DataSource.Factory dataSource = new DefaultDataSourceFactory(context, userAgent);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSource).createMediaSource(videoUri);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.addListener(this);

            mediaSessionCompat = new MediaSessionCompat(context, "StepActivityMediaSession");
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


    }

    protected void releasePlayer(){
        if (simpleExoPlayer != null){
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        if (mediaSessionCompat != null){
            mediaSessionCompat.setActive(false);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
//            audioManager.abandonAudioFocusRequest(audioFocusRequest.build());
//        } else if (audioFocusChangeListener != null){
//            audioManager.abandonAudioFocus(audioFocusChangeListener);
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



    protected void requestAudioFocus(){
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
    public void goToBackground(){
        if(simpleExoPlayer != null){
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.setPlayWhenReady(false);
//            currentPosition = simpleExoPlayer.getCurrentPosition();
        }
    }

    public void goToForeground(){
        if(simpleExoPlayer != null){
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
        }

    }


}
