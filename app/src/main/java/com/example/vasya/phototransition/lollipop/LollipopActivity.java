package com.example.vasya.phototransition.lollipop;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.vasya.phototransition.R;
import com.example.vasya.phototransition.utils.LoaderCallback;
import com.example.vasya.phototransition.utils.ProjectUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

@TargetApi(21)
public class LollipopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestFeature();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        postponeEnterTransition();  //postpone the transition
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }

        final ImageView image = (ImageView) (findViewById(R.id.image));
        final int resourceId = savedInstanceState.getInt(ProjectUtils.DATA,-1);
        final String transitionName = savedInstanceState.getString(ProjectUtils.KEY);
        final boolean isPicasso=savedInstanceState.getBoolean(ProjectUtils.PICASSO);

        image.setTransitionName(transitionName);
        if(resourceId>0){
            if(isPicasso) {
                Picasso.with(this).load(resourceId)
                        .fit().centerCrop().into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        startTransition(image);
                    }

                    @Override
                    public void onError() {
                        startTransition(image);
                    }
                });
            }else {
                Glide.with(this)
                        .load(resourceId).asBitmap().centerCrop().
                        listener(new LoaderCallback<Integer, Bitmap>(image) {
                            @Override
                            public void onReady(ImageView image) {
                                startTransition(image);
                            }
                        }).into(image);
            }
        }

    }

    private void startTransition(final ImageView image) {
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }


    @TargetApi(21)
    private void requestFeature() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        TransitionSet set=new TransitionSet();
        set.addTransition(new ChangeBounds());
        set.addTransition(new ChangeClipBounds());
        set.addTransition(new ChangeImageTransform());
        set.setDuration(getResources().getInteger(R.integer.duration));
        set.setInterpolator(new DecelerateInterpolator());
        getWindow().setSharedElementEnterTransition(set);
        getWindow().setSharedElementsUseOverlay(false);
    }
}
