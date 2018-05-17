package com.example.android.bakingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    private RecipeAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private Parcelable savedLayoutState;
    private boolean isDualPane;
    private static final int LOADER_ID = 1;
    public static final String KEY_RECIPE = "recipe";
    private static final String KEY_GRID_LAYOUT_STATE = "grid_layout_state";
    private static final String KEY_LIST_LAYOUT_STATE = "list_layout_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_recipe);
        emptyView.setVisibility(View.INVISIBLE);



        RecyclerView listRecyclerView = findViewById(R.id.recycler_view_list);
        isDualPane = listRecyclerView == null;

        adapter = new RecipeAdapter(this);
        if (isDualPane){
            RecyclerView gridRecyclerView = findViewById(R.id.recycler_view_grid);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float columnSize = getResources().getDimension(R.dimen.grid_column_width);
            float spanCount = displayMetrics.widthPixels / columnSize;
            gridLayoutManager = new GridLayoutManager(this, (int)spanCount);
            gridRecyclerView.setLayoutManager(gridLayoutManager);
            gridRecyclerView.setHasFixedSize(true);
            if (savedInstanceState != null){
                savedLayoutState = savedInstanceState.getParcelable(KEY_GRID_LAYOUT_STATE);
            }
            gridRecyclerView.setAdapter(adapter);
        } else {
            linearLayoutManager = new LinearLayoutManager(this);
            listRecyclerView.setLayoutManager(linearLayoutManager);
            listRecyclerView.setHasFixedSize(true);
            if (savedInstanceState != null){
                savedLayoutState = savedInstanceState.getParcelable(KEY_LIST_LAYOUT_STATE);
            }
            listRecyclerView.setAdapter(adapter);
        }

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
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (connectivityManager != null) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
            }
            if (activeNetwork == null || !activeNetwork.isConnected()) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            }


        return new RecipeAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {
        progressBar.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
        adapter.setRecipes(recipes);
        if (savedLayoutState != null && isDualPane){
            gridLayoutManager.onRestoreInstanceState(savedLayoutState);
        } else if (savedLayoutState != null){
            linearLayoutManager.onRestoreInstanceState(savedLayoutState);
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
        if (isDualPane){
            outState.putParcelable(KEY_GRID_LAYOUT_STATE, gridLayoutManager.onSaveInstanceState());
        } else {
            outState.putParcelable(KEY_LIST_LAYOUT_STATE, linearLayoutManager.onSaveInstanceState());
        }

    }
}
