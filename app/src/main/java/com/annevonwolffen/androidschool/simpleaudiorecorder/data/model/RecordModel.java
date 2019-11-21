package com.annevonwolffen.androidschool.simpleaudiorecorder.data.model;

public class RecordModel {
    private String mName;
    private String mDuration;

    private boolean mIsPlaying;

    public RecordModel(String name, String duration) {
        this.mName = name;
        this.mDuration = duration;
    }

    public String getName() {
        return mName;
    }

    public String getDuration() {
        return mDuration;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.mIsPlaying = isPlaying;
    }
}