package com.vasya.phototransition.utils;

import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public abstract class LoaderCallback<T,M>
    implements RequestListener<T,M> {

    private ImageView sharedImage;

    public LoaderCallback(ImageView sharedImage) {
        this.sharedImage=sharedImage;
    }

    @Override
    public boolean onException(Exception e, T model, Target<M> target, boolean isFirstResource) {

        return false;
    }

    @Override
    public boolean onResourceReady(M resource, T model, Target<M> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if(isFirstResource) {
            onReady(sharedImage);
        }
        return false;
    }

    public abstract void onReady(ImageView image);
}
