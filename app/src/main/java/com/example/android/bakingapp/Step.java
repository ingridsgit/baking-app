package com.example.android.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;


public class Step implements Parcelable {

    private final int id;
    private final String shortDesc;
    private final String description;
    private final String videoUrl;
    private final String thumbnailUrl;

    public Step(int id, String shortDesc, String description, String videoUrl, String thumbnailUrl) {
        this.id = id;
        this.shortDesc = shortDesc;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    private Step(Parcel in) {
        id = in.readInt();
        shortDesc = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        thumbnailUrl = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(shortDesc);
        parcel.writeString(description);
        parcel.writeString(videoUrl);
        parcel.writeString(thumbnailUrl);

    }

    public int getId() {
        return id;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
