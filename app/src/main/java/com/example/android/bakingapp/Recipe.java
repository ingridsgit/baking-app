package com.example.android.bakingapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ingrid on 3/11/2018.
 */

public class Recipe implements Parcelable{
    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private ArrayList<Step> steps;
    private int servings;
    private String imagePath;

    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, @Nullable String imagePath){
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imagePath = imagePath;
    }

    public Recipe(Parcel in){
        id = in.readInt();
        name = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
        servings = in.readInt();
        imagePath = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedList(ingredients);
        parcel.writeTypedList(steps);
        parcel.writeInt(servings);
        parcel.writeString(imagePath);

    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }
    
    public Step getStepById(int stepId){
        for ( Step step: steps ) {
         if (step.getId() == stepId){
             return step;
         }
        }
        return null;
    }

    public int getServings() {
        return servings;
    }
}
