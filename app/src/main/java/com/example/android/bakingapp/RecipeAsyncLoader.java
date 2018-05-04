package com.example.android.bakingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



import com.example.android.bakingapp.Utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Ingrid on 3/16/2018.
 */

public class RecipeAsyncLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

    private Context context;


    public RecipeAsyncLoader(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Recipe> loadInBackground() {
        return NetworkUtils.getDataFromWeb(context);
    }
}
