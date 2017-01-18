package com.vasya.phototransition.prelollipop;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.vasya.phototransition.R;
import com.vasya.phototransition.utils.LoaderCallback;
import com.vasya.phototransition.utils.ProjectUtils;
import com.vpaliy.transition.AnimatedImageView;
import com.vpaliy.transition.TransitionAnimation;
import com.vpaliy.transition.TransitionRunner;


public class PreLollipopActivity extends AppCompatActivity {

    private static final String TAG=PreLollipopActivity.class.getSimpleName();

    private TransitionRunner runner;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }

        final AnimatedImageView animatedImageView=(AnimatedImageView) (findViewById(R.id.image));
        final int resourceId=savedInstanceState.getInt(ProjectUtils.DATA,-1);

        if(resourceId>0){
            Glide.with(this)
                    .load(resourceId)
                    .asBitmap()
                    .centerCrop()
                    //to prevent the animation from shuddering, use the listener to track when the image is ready,
                    // otherwise you may start the animation when the resource hasn't been loaded yet
                    .listener(new LoaderCallback<Integer, Bitmap>(animatedImageView) {
                        @Override
                        public void onReady(ImageView image) {
                            if(getIntent()!=null) {
                                initAnimator(getIntent(), animatedImageView);
                            }
                        }
                    })
                    .into(animatedImageView);
        }
    }

    private void initAnimator(final Intent intent, final AnimatedImageView image) {
        ViewGroup container=(ViewGroup)(image.getParent());
        runner = TransitionRunner.with(intent).
            target(image).fadeContainer(container)
            .duration(getResources().getInteger(R.integer.duration));
        runner.run(TransitionAnimation.ENTER);
    }

    @Override
    public void onBackPressed() {
        //if the animation has occurred, go ahead and start animating the image backwards
        if(runner!=null) {
            runner.runAway(TransitionAnimation.EXIT,this);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
