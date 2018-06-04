package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

//import com.squareup.picasso.Picasso;


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
            if (thumbnailPath != null) {
                if (thumbnailPath.isEmpty()) {
                    thumbnail.setVisibility(View.GONE);
//                } else {
//                    Picasso.get()
//                            .load(thumbnailPath)
//                            .error(R.drawable.ic_image_black_48dp)
//                            .into(thumbnail);
                }
            }


            TextView shortDescView = convertView.findViewById(R.id.short_desc_text_view);
            String shortDesc = currentStep.getShortDesc();
            shortDescView.setText(shortDesc);

        return convertView;
    }
}
