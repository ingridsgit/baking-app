package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ingrid on 3/25/2018.
 */

public class IngredientAdapter extends ArrayAdapter<Ingredient>{

    public IngredientAdapter(@NonNull Context context, @NonNull List<Ingredient> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_list_item, parent, false);
        }
        Ingredient currentIngredient = getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.ingredient_name_text_view);
        nameTextView.setText(currentIngredient.getName());

        TextView quantityView = convertView.findViewById(R.id.ingredient_quantity_text_view);
        quantityView.setText(String.valueOf(currentIngredient.getQuantity()));

        TextView measureTextView = convertView.findViewById(R.id.ingredient_measure_text_view);
        String measure = currentIngredient.getMeasure();
        if (measure.equalsIgnoreCase("unit")){
            measureTextView.setVisibility(View.GONE);
        }
        measureTextView.setText(measure);

        return convertView;
    }
}