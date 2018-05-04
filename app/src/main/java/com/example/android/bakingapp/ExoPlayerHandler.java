package com.example.android.bakingapp;

public class ExoPlayerHandler {

    private static ExoPlayerHandler exoPlayerHandler;

    public static ExoPlayerHandler getExoPlayerHandler() {
        if (exoPlayerHandler == null){
            exoPlayerHandler = new ExoPlayerHandler();
        }
        return exoPlayerHandler;
    }

}
