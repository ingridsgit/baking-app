package com.example.android.bakingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

class RecipeAsyncLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

    public RecipeAsyncLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Recipe> loadInBackground() {
        return NetworkUtils.getDataFromWeb();
    }
}
