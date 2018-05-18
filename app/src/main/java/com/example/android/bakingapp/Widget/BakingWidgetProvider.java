package com.example.android.bakingapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.android.bakingapp.DetailActivity;
import com.example.android.bakingapp.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Recipe;
import com.example.android.bakingapp.Utils.NetworkUtils;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

//    Widget displays ingredient list for desired recipe.

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (int appWidgetId : appWidgetIds) {

            String recipeName = sharedPreferences.getString(DetailActivity.KEY_RECIPE_NAME, null);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);
            if (recipeName != null){
                views.setTextViewText(R.id.widget_recipe_text_view, recipeName);
                Intent openActivityIntent = new Intent(context, RecipeOpenerService.class);
                openActivityIntent.setAction(RecipeOpenerService.ACTION_OPEN_RECIPE);
                openActivityIntent.putExtra(DetailActivity.KEY_RECIPE_NAME, recipeName);
                PendingIntent pendingIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    pendingIntent = PendingIntent.getService(context, 0, openActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                }
                views.setOnClickPendingIntent(R.id.widget_recipe_text_view, pendingIntent);
            } else {
                views.setViewVisibility(R.id.widget_recipe_text_view, View.GONE);
            }

            Intent adapterIntent = new Intent(context, ListWidgetService.class);
            views.setRemoteAdapter(R.id.widget_list_view, adapterIntent);
            views.setEmptyView(R.id.widget_list_view, R.id.empty_view);



            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

