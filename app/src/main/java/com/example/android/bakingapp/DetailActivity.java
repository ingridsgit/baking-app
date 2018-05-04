package com.example.android.bakingapp;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private Recipe selectedRecipe;
    private Step selectedStep;
    private boolean isDualPane;
    private ListView ingredientListView;
    private ListView stepListView;
    public static final String KEY_SELECTED_STEP = "selected_step";
    public static final String KEY_SELECTED_RECIPE = "selected_recipe";
    private static final String KEY_INGREDIENT_LIST_STATE = "ingredient_state";
    private static final String KEY_STEP_LIST_STATE = "step_list_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent starterIntent = getIntent();
        selectedRecipe = starterIntent.getParcelableExtra(MainActivity.KEY_RECIPE);

        View sideFragmentView = findViewById(R.id.side_step_fragment);
        isDualPane = sideFragmentView != null && sideFragmentView.getVisibility()== View.VISIBLE;

        if (isDualPane && savedInstanceState == null){
            StepFragment stepFragment = StepFragment.newInstance(null);
            getSupportFragmentManager().beginTransaction().add(R.id.side_step_fragment, stepFragment).commit();
        } else if (savedInstanceState != null){
            selectedStep = savedInstanceState.getParcelable(KEY_SELECTED_STEP);
           if (isDualPane){
                StepFragment stepFragment = StepFragment.newInstance(selectedStep);
                getSupportFragmentManager().beginTransaction().replace(R.id.side_step_fragment, stepFragment).commit();
            }

        }


//        else if (!isDualPane){
//            Fragment stepFragment = getSupportFragmentManager().findFragmentById(R.id.side_step_fragment);
//            getSupportFragmentManager().beginTransaction().remove(stepFragment).commit();
//        }

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

        TextView recipeName = findViewById(R.id.recipe_name_detail);
        recipeName.setText(selectedRecipe.getName());

        TextView servingTextView = findViewById(R.id.servings_text_view);
        servingTextView.setText(getString(R.string.servings, selectedRecipe.getServings() ));

        ArrayList<Ingredient> ingredients = selectedRecipe.getIngredients();
        if (ingredients != null){
            IngredientAdapter ingredientAdapter = new IngredientAdapter(this, ingredients);
            ingredientListView = findViewById(R.id.ingredient_list);
            ingredientListView.setAdapter(ingredientAdapter);
            ingredientListView.setDivider(null);

        }

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
        outState.putParcelable(KEY_INGREDIENT_LIST_STATE, ingredientListView.onSaveInstanceState());
        outState.putParcelable(KEY_STEP_LIST_STATE, stepListView.onSaveInstanceState());
        outState.putParcelable(KEY_SELECTED_STEP, selectedStep);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            ingredientListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_LIST_STATE));
            stepListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_STEP_LIST_STATE));

        }

    }
}
