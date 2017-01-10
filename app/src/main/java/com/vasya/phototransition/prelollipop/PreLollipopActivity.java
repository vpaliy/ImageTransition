package com.vasya.phototransition.prelollipop;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vasya.phototransition.R;
import com.vasya.phototransition.utils.LoaderCallback;
import com.vasya.phototransition.utils.ProjectUtils;
import com.vpaliy.transition.AnimatedImageView;
import com.vpaliy.transition.ImageState;
import com.vpaliy.transition.TransitionAnimation;
import com.vpaliy.transition.TransitionListener;
import com.vpaliy.transition.TransitionRunner;
import com.vpaliy.transition.TransitionStarter;


public class PreLollipopActivity extends AppCompatActivity {

    private static final String TAG=PreLollipopActivity.class.getSimpleName();

    private TransitionRunner runner;
    private ObjectAnimator backgroundAnimator;
    private ColorDrawable background=new ColorDrawable(Color.WHITE);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }

        final AnimatedImageView image=(AnimatedImageView) (findViewById(R.id.image));
        final int resourceId=savedInstanceState.getInt(ProjectUtils.DATA,-1);
        final ImageState prevImageState=savedInstanceState.getParcelable(TransitionStarter.IMAGE_STATE);
        final int startPosition=savedInstanceState.getInt(ProjectUtils.CURRENT_POSITION);

        if(resourceId>0){
            Glide.with(this)
                    .load(resourceId)
                    .asBitmap()
                    .fitCenter()
                    //to prevent the animation from shuddering, use the listener to track when the image is ready,
                    // otherwise you may start the animation when the resource hasn't been loaded yet
                    .listener(new RequestListener<Integer, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, Integer model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Integer model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if(isFirstResource) {
                                image.setImageBitmap(resource);
                                onReady(image);
                            }
                            return true;
                        }

                        public void onReady(AnimatedImageView image) {
                            if(prevImageState!=null) {
                                initAnimator(getIntent(), image);
                            }
                        }
                    })
                    .into(image);
        }
    }

    private void initAnimator(final Intent intent, final AnimatedImageView image) {
        runner = TransitionRunner.with(intent).
            target(image).addListener(new TransitionListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                ViewGroup parent=(ViewGroup)(image.getParent());
                parent.setBackgroundDrawable(background);
                backgroundAnimator=ObjectAnimator.ofInt(background,"alpha",0,255);
                backgroundAnimator.setDuration(animator.getDuration());
                backgroundAnimator.setInterpolator(new DecelerateInterpolator());
                backgroundAnimator.start();
            }
        }).duration(getResources().getInteger(R.integer.duration));
        runner.run(TransitionAnimation.ENTER);
    }

    @Override
    public void onBackPressed() {
        //if the animation has occurred, go ahead and start animating the image backwards
        if(runner!=null) {
            runner.clearListeners();
            //set listeners to control the animation here
            runner.addListener(new TransitionListener() {

                //if you previously have changed the background of parent view,
                // add this code in order to change the background of parent view to transparency
                // thus the user can see where the animated image is going
                @Override
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    backgroundAnimator.setDuration(animator.getDuration());
                    backgroundAnimator.reverse();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    finish();
                    overridePendingTransition(0,0);
                }
            });
            runner.run(TransitionAnimation.EXIT);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
