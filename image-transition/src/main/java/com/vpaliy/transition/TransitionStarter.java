package com.vpaliy.transition;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.ImageView;


public final class TransitionStarter {

    private static final String TAG= TransitionStarter.class.getSimpleName();

    private Activity startActivity;
    private ImageView startImage;
    private int startPosition=-1;   //default value

    public static final String START_POSITION="startPosition"+TAG;
    public static final String IMAGE_STATE="imageState"+ TAG;

    private TransitionStarter(@NonNull Activity startActivity) {
        this.startActivity=startActivity;
        //Has to be private
    }

    public static TransitionStarter with(@NonNull Activity startActivity) {
        return new TransitionStarter(startActivity);
    }

    public TransitionStarter from(@NonNull AnimatedImageView startImage) {
        this.startImage=startImage;
        return this;
    }

    private void initIntent(Intent intent) {
        intent.putExtra(START_POSITION,startPosition);
        intent.putExtra(IMAGE_STATE, ImageState.newInstance(startImage));
    }

    public void start(@NonNull Intent intent, int startPosition) {
        this.startPosition=startPosition;
        start(intent);
    }

    public void start(@NonNull Intent intent) {
        initIntent(intent);
        startActivity.startActivity(intent);
        startActivity.overridePendingTransition(0,0);
    }

    public void startForResult(@NonNull Intent intent, int resultCode, int startPosition) {
        this.startPosition=startPosition;
        startForResult(intent,resultCode);
    }

    public void startForResult(@NonNull Intent intent, int resultCode) {
        initIntent(intent);
        startActivity.startActivityForResult(intent,resultCode);
        startActivity.overridePendingTransition(0,0);
    }

}
