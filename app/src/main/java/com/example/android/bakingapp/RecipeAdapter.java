package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String EMPTY_PATH = "empty";
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private final RecipeClickHandler recipeClickHandler;


    public RecipeAdapter(RecipeClickHandler recipeClickHandler){
        this.recipeClickHandler = recipeClickHandler;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View inflatedView = inflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe currentRecipe = recipes.get(position);
        holder.recipeName.setText(currentRecipe.getName());
        String imagePath = currentRecipe.getImagePath();

        if (imagePath == null || imagePath.isEmpty()){
            imagePath = EMPTY_PATH;
        }
        Picasso.get()
                .load(imagePath)
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.recipeImage);
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }


    public interface RecipeClickHandler{
        void onRecipeClick(Recipe recipe);
    }


    @Override
    public int getItemCount() {
        if (recipes == null){
            return 0;
        } else {
            int size = recipes.size();
            return size ;}
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView recipeImage;
        private final TextView recipeName;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Recipe clickedRecipe = recipes.get(adapterPosition);
            recipeClickHandler.onRecipeClick(clickedRecipe);
        }
    }
}
