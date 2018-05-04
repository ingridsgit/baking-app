package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class StepAdapter extends ArrayAdapter<Step> {

    public StepAdapter(@NonNull Context context, @NonNull List<Step> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.step_list_item, parent, false);
        }

        final Step currentStep = getItem(position);

        ImageView thumbnail = convertView.findViewById(R.id.thumbnail_image_view);
        String thumbnailPath = currentStep.getThumbnailUrl();
        if (thumbnailPath == null || thumbnailPath.isEmpty()){
            thumbnail.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(thumbnailPath)
                    .error(R.drawable.ic_image_black_48dp)
                    .into(thumbnail);
        }

        TextView shortDescView = convertView.findViewById(R.id.short_desc_text_view);
        shortDescView.setText(currentStep.getShortDesc());

        return convertView;
    }
}
