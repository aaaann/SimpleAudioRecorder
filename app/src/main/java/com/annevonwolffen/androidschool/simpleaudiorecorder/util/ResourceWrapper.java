package com.annevonwolffen.androidschool.simpleaudiorecorder.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;

public class ResourceWrapper {
    private final Resources mResources;

    public ResourceWrapper(@NonNull Resources resources) {
        mResources = resources;
    }


    public Drawable getPlayIcon() {
        return mResources.getDrawable(R.drawable.ic_play_circle_outline_black_24dp);
    }

    public Drawable getPauseIcon() {
        return mResources.getDrawable(R.drawable.ic_pause_black_24dp); //todo: change icon to circled_outline
    }
}
