package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.Widget.BakingWidgetProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DetailActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Recipe selectedRecipe;
    private Step selectedStep;
    private boolean isDualPane;
    private ListView ingredientListView;
    private ListView stepListView;
    private SharedPreferences sharedPreferences;
    public static final String KEY_SELECTED_STEP = "selected_step";
    public static final String KEY_SELECTED_RECIPE = "selected_recipe";
    private static final String KEY_INGREDIENT_LIST_STATE = "ingredient_state";
    private static final String KEY_STEP_LIST_STATE = "step_list_state";
    public static final String KEY_INGREDIENT_LIST = "ingredient_list";
    public static final String KEY_RECIPE_NAME = "recipe_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View sideFragmentView = findViewById(R.id.side_step_fragment);
        isDualPane = sideFragmentView != null && sideFragmentView.getVisibility()== View.VISIBLE;

        if (savedInstanceState == null){
            Intent starterIntent = getIntent();
            selectedRecipe = starterIntent.getParcelableExtra(MainActivity.KEY_RECIPE);
            if (isDualPane){
                StepFragment stepFragment = StepFragment.newInstance(null);
                getSupportFragmentManager().beginTransaction().add(R.id.side_step_fragment, stepFragment).commit();
            }
        } else {
            selectedRecipe = savedInstanceState.getParcelable(KEY_SELECTED_RECIPE);
            selectedStep = savedInstanceState.getParcelable(KEY_SELECTED_STEP);
            if (isDualPane){
                StepFragment stepFragment = StepFragment.newInstance(selectedStep);
                getSupportFragmentManager().beginTransaction().replace(R.id.side_step_fragment, stepFragment).commit();
            }
        }


        setTitle(selectedRecipe.getName());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Setup the recipe image
        ImageView recipeImage = findViewById(R.id.recipe_image_view);
        String imagePath = selectedRecipe.getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            recipeImage.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(imagePath)
                    .error(R.drawable.ic_image_black_48dp)
                    .into(recipeImage);
        }
        // Display the ingredients
        TextView servingTextView = findViewById(R.id.servings_text_view);
        servingTextView.setText(getString(R.string.servings, selectedRecipe.getServings() ));

        ArrayList<Ingredient> ingredients = selectedRecipe.getIngredients();
        if (ingredients != null){
            IngredientAdapter ingredientAdapter = new IngredientAdapter(this, ingredients);
            ingredientListView = findViewById(R.id.ingredient_list);
            ingredientListView.setAdapter(ingredientAdapter);
            ingredientListView.setDivider(null);
        }

        //Display the steps
        final ArrayList<Step> steps = selectedRecipe.getSteps();
        if (steps != null){
            StepAdapter stepAdapter = new StepAdapter(this, steps);
            stepListView = findViewById(R.id.step_list);
            stepListView.setAdapter(stepAdapter);
            stepListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedStep = steps.get(i);
                    if (isDualPane){
                        StepFragment stepFragment = StepFragment.newInstance(selectedStep);
                        getSupportFragmentManager().beginTransaction().replace(R.id.side_step_fragment, stepFragment).commit();
                    } else {
                        Intent stepDetails = new Intent(getApplicationContext(), StepActivity.class);
                        stepDetails.putExtra(KEY_SELECTED_STEP, selectedStep);
                        stepDetails.putExtra(KEY_SELECTED_RECIPE, selectedRecipe);
                        getApplicationContext().startActivity(stepDetails);
                    }
                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SELECTED_RECIPE, selectedRecipe);
        outState.putParcelable(KEY_SELECTED_STEP, selectedStep);
        outState.putParcelable(KEY_INGREDIENT_LIST_STATE, ingredientListView.onSaveInstanceState());
        outState.putParcelable(KEY_STEP_LIST_STATE, stepListView.onSaveInstanceState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){

            // pick the recipe for the widget
            case R.id.selected:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(KEY_INGREDIENT_LIST, getIngredientStringList());
                editor.putString(KEY_RECIPE_NAME, selectedRecipe.getName());
                editor.apply();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            ingredientListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_LIST_STATE));
            stepListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_STEP_LIST_STATE));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(KEY_INGREDIENT_LIST)) {

            // Update the widget with the newly selected recipe
            Intent intentUpdate = new Intent(this, BakingWidgetProvider.class);
            intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidgetProvider.class));
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(intentUpdate);
        }
    }

    // Convert ArrayList<Ingredient> to Set<String>
    private Set<String> getIngredientStringList(){
        ArrayList<Ingredient> ingredientArrayList = selectedRecipe.getIngredients();
        String ingredientString;
        Set<String> ingredientStringSet = new HashSet<>();
        for (Ingredient ingredient : ingredientArrayList) {
            String name = ingredient.getName();
            String quantity = String.valueOf(ingredient.getQuantity());
            String measure = ingredient.getMeasure();
            ingredientString = quantity + " " + measure + " " + name;
            ingredientStringSet.add(ingredientString);
        }
        return ingredientStringSet;
    }
}
