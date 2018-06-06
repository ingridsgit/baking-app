package com.example.android.bakingapp;

//Copyright 2013 Square, Inc.
//
//        Licensed under the Apache License, Version 2.0 (the "License");
//        you may not use this file except in compliance with the License.
//        You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//        Unless required by applicable law or agreed to in writing, software
//        distributed under the License is distributed on an "AS IS" BASIS,
//        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//        See the License for the specific language governing permissions and
//        limitations under the License.

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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


    public RecipeAdapter(RecipeClickHandler recipeClickHandler) {
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

        if (imagePath == null || imagePath.isEmpty()) {
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


    public interface RecipeClickHandler {
        void onRecipeClick(Recipe recipe);
    }


    @Override
    public int getItemCount() {
        if (recipes == null) {
            return 0;
        } else {
            return recipes.size();
        }
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView recipeImage;
        private final TextView recipeName;

        RecipeViewHolder(View itemView) {
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
