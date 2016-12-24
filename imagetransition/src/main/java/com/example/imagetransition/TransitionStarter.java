package com.example.imagetransition;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.ImageView;

public final class TransitionStarter {

    private static final String TAG=TransitionStarter.class.getSimpleName();

    private Activity startActivity;
    private ImageView startImage;

    public static final String IMAGE_STATE="IMAGE_STATE";

    private TransitionStarter(Activity startActivity) {
        this.startActivity=startActivity;
        //Has to be private
    }

    public static TransitionStarter with(@NonNull Activity startActivity) {
        return new TransitionStarter(startActivity);
    }

    public TransitionStarter from(@NonNull ImageView startImage) {
        this.startImage=startImage;
        return this;
    }

    private void initIntent(Intent intent) {
        intent.putExtra(IMAGE_STATE,ImageState.newInstance(startImage));
    }


    public void start(@NonNull Intent intent) {
        initIntent(intent);
        startActivity.startActivity(intent);
        startActivity.overridePendingTransition(0,0);
    }

    public void startForResult(@NonNull Intent intent, int resultCode) {
        initIntent(intent);
        startActivity.startActivityForResult(intent,resultCode);
        startActivity.overridePendingTransition(0,0);
    }

}
