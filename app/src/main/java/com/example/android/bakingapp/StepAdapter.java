package com.example.android.bakingapp;

//Copyright 2017 Bogdan Kornev.
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

import android.content.Context;
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


class StepAdapter extends ArrayAdapter<Step> {

    private static final String EMPTY_PATH = "empty";

    public StepAdapter(@NonNull Context context, @NonNull List<Step> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.step_list_item, parent, false);
        }

        final Step currentStep = getItem(position);

        ImageView thumbnail = convertView.findViewById(R.id.thumbnail_image_view);
        String thumbnailPath = currentStep.getThumbnailUrl();
        if (thumbnailPath != null) {
            if (thumbnailPath.isEmpty()) {
                thumbnail.setVisibility(View.GONE);
                thumbnailPath = EMPTY_PATH;
            }
            Picasso.get()
                    .load(thumbnailPath)
                    .error(R.drawable.ic_image_black_48dp)
                    .into(thumbnail);
        }


        TextView shortDescView = convertView.findViewById(R.id.short_desc_text_view);
        String shortDesc = currentStep.getShortDesc();
        shortDescView.setText(shortDesc);

        return convertView;
    }
}
