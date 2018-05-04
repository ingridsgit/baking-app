package com.example.android.bakingapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    private RecipeAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private GridLayoutManager gridLayoutManager;
    private Parcelable savedLayoutState;
    private static final int LOADER_ID = 1;
    public static final String KEY_RECIPE = "recipe";
    private static final String KEY_LAYOUT_STATE = "layout_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_recipe);
        emptyView.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 2);
        //TODO: change to spancount to adaptative layout
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        if (savedInstanceState != null){
            savedLayoutState = savedInstanceState.getParcelable(KEY_LAYOUT_STATE);
        }

        adapter = new RecipeAdapter(this);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent goToRecipeDetail = new Intent(MainActivity.this, DetailActivity.class);
        goToRecipeDetail.putExtra(KEY_RECIPE, recipe);
        startActivity(goToRecipeDetail);
    }


    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int i, Bundle bundle) {
        return new RecipeAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {
        progressBar.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
        adapter.setRecipes(recipes);
        if (savedLayoutState != null){
            gridLayoutManager.onRestoreInstanceState(savedLayoutState);
        }
        if (recipes == null || recipes.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAYOUT_STATE, gridLayoutManager.onSaveInstanceState());
    }
}
