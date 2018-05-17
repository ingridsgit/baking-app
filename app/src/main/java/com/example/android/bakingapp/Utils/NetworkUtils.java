package com.example.android.bakingapp.Utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Recipe;
import com.example.android.bakingapp.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by Ingrid on 3/10/2018.
 */

public final class NetworkUtils {

        private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
        private static final String URI_BASE = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
        private static final String KEY_ID = "id";
        private static final String KEY_NAME = "name";
        private static final String KEY_INGREDIENTS = "ingredients";
        private static final String KEY_SERVINGS = "servings";
        private static final String KEY_IMAGE = "image";
        private static final String KEY_QUANTITY = "quantity";
        private static final String KEY_MEASURE = "measure";
        private static final String KEY_INGREDIENT = "ingredient";
        private static final String KEY_STEPS = "steps";
        private static final String KEY_SHORT_DESC = "shortDescription";
        private static final String KEY_DESCRIPTION = "description";
        private static final String KEY_VIDEO_URL = "videoURL";
        private static final String KEY_THUMBNAIL_URL = "thumbnailUrl";

        public static Recipe getSingleRecipeFromWeb(Context context, String recipeName){
            URL url = buildUrl(context);
            InputStream inputStream = makeHttpRequest(url, context);
            if (inputStream != null){
                String response = readFromStream(inputStream);
                if (response != null) {
                    try {
                        JSONArray results = new JSONArray(response);
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject recipe = results.optJSONObject(i);
                            String name = recipe.optString(KEY_NAME);
                            if (name.equals(recipeName)){
                                int recipeId = recipe.optInt(KEY_ID);
                                ArrayList<Ingredient> ingredients = extractIngredientsFromJson(recipe.optJSONArray(KEY_INGREDIENTS));
                                ArrayList<Step> steps = extractStepsFromJson(recipe.optJSONArray(KEY_STEPS));
                                int servings = recipe.optInt(KEY_SERVINGS);
                                String image = recipe.optString(KEY_IMAGE);
                                return new Recipe(recipeId, name, ingredients, steps, servings, image);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } return null;
        }

        public static ArrayList<Recipe> getDataFromWeb(Context context) {
            URL url = buildUrl(context);
            InputStream inputStream = makeHttpRequest(url, context);
            if (inputStream != null){
                String response = readFromStream(inputStream);
                return extractRecipesFromJson(response);
            } else return null;
        }


//        static ArrayList<Step> getRecipeSteps(Context context, int recipeId) {
//            URL url = buildUrl(context);
//            InputStream inputStream = makeHttpRequest(url);
//            if (inputStream != null){
//                String response = readFromStream(inputStream);
//                return extractStepsFromJson(response, recipeId);
//            } else return null;
//        }

        private static URL buildUrl(Context context) {
            URL url = null;
            try {
                url = new URL(URI_BASE);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return url;
        }


        private static InputStream makeHttpRequest(URL url, Context context) {
            HttpURLConnection httpURLConnection;
            InputStream inputStream = null;
            int responseCode;
            if (url != null) {
                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setConnectTimeout(15000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();
                    responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == 200) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        Log.e(LOG_TAG, "Request impossible. Response code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    return null;

                }
            }
            return inputStream;
        }

        private static String readFromStream(InputStream inputStream) {
            StringBuilder jsonResponse = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                try {
                    line = bufferedReader.readLine();
                    while (line != null) {
                        jsonResponse.append(line);
                        line = bufferedReader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonResponse.toString();
        }

        private static ArrayList<Recipe> extractRecipesFromJson(String jsonResponse) {
            ArrayList<Recipe> recipes = new ArrayList<>();
            if (jsonResponse != null) {
                try {
                    JSONArray results = new JSONArray(jsonResponse);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject recipe= results.optJSONObject(i);
                        int recipeId = recipe.optInt(KEY_ID);
                        String name = recipe.optString(KEY_NAME);
                        ArrayList<Ingredient> ingredients = extractIngredientsFromJson(recipe.optJSONArray(KEY_INGREDIENTS));
                        ArrayList<Step> steps = extractStepsFromJson(recipe.optJSONArray(KEY_STEPS));
                        int servings = recipe.optInt(KEY_SERVINGS);
                        String image = recipe.optString(KEY_IMAGE);
                        recipes.add(new Recipe(recipeId, name, ingredients, steps, servings, image));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return recipes;
        }

        private static ArrayList<Ingredient> extractIngredientsFromJson(JSONArray ingredientsJson) {
            ArrayList<Ingredient> ingredientsList = new ArrayList<>();
            if (ingredientsJson != null) {
                for (int i = 0; i < ingredientsJson.length(); i++) {
                    JSONObject item = ingredientsJson.optJSONObject(i);
                    int quantity = item.optInt(KEY_QUANTITY);
                    String measure = item.optString(KEY_MEASURE);
                    String ingredient = item.optString(KEY_INGREDIENT);
                    ingredientsList.add(new Ingredient(quantity, measure, ingredient));
                }
            }
            return ingredientsList;
        }

        private static ArrayList<Step> extractStepsFromJson(JSONArray stepsJson) {
            ArrayList<Step> stepsList = new ArrayList<>();
            if (stepsJson != null) {
                    for (int i = 0; i < stepsJson.length(); i++) {
                        JSONObject item = stepsJson.optJSONObject(i);
                        int id = item.optInt(KEY_ID);
                        String shortDesc = item.optString(KEY_SHORT_DESC);
                        String description = item.optString(KEY_DESCRIPTION);
                        String videoUrl = item.optString(KEY_VIDEO_URL);
                        String thumbnailUrl = item.optString(KEY_THUMBNAIL_URL);
                        stepsList.add(new Step(id, shortDesc, description, videoUrl, thumbnailUrl));
                    }
            }
            return stepsList;
        }

    }

