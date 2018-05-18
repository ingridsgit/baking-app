package com.example.android.bakingapp.Widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.DetailActivity;
import com.example.android.bakingapp.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Recipe;
import com.example.android.bakingapp.Utils.NetworkUtils;

public class RecipeOpenerService extends IntentService {

    public static final String ACTION_OPEN_RECIPE = "com.example.android.bakingapp.Widget.action.open_recipe";



    public RecipeOpenerService() {
        super("RecipeOpenerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){

            String action = intent.getAction();

            if (action.equals(ACTION_OPEN_RECIPE)){
                final String recipeName = intent.getStringExtra(DetailActivity.KEY_RECIPE_NAME);
                final Recipe recipe = NetworkUtils.getSingleRecipeFromWeb(getApplicationContext(), recipeName);
                if (recipe != null){
                    Intent openIntent = new Intent(getApplicationContext(), DetailActivity.class);
                    openIntent.putExtra(MainActivity.KEY_RECIPE, recipe);
                    openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(openIntent);
                } else {
                    toast(getText(R.string.error));

                }
            }
        }
    }


private void toast(final CharSequence text){
    Handler toastHandler = new Handler(getMainLooper());
    toastHandler.post(new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getBaseContext(),text , Toast.LENGTH_LONG).show();
        }
    });
}


}
