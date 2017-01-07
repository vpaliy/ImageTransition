package com.vasya.phototransition.utils;


import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.vasya.phototransition.R;

public class GlideModuleQ implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glideRequest);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}
