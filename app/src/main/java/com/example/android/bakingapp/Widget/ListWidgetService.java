package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.DetailActivity;
import com.example.android.bakingapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private Set<String> ingredientStringSet;

    ListRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ingredientStringSet = sharedPreferences.getStringSet(DetailActivity.KEY_INGREDIENT_LIST, null);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredientStringSet == null) {
            return 0;
        } else {
            return ingredientStringSet.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        List<String> ingredientArrayList = new ArrayList<>(ingredientStringSet);
        remoteViews.setTextViewText(R.id.widget_list_item, ingredientArrayList.get(i));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
