package com.vasya.phototransition.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.vasya.phototransition.R;
import com.vasya.phototransition.utils.Constants;
import com.vpaliy.transition.AnimatedImageView;
import com.vpaliy.transition.TransitionAnimation;
import com.vpaliy.transition.TransitionRunner;
import android.support.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PreLollipopActivity extends AppCompatActivity {


    private TransitionRunner runner;

    @BindView(R.id.image)
    protected AnimatedImageView image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        ButterKnife.bind(this);
        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }

        loadImage(savedInstanceState.getInt(Constants.DATA,-1));
    }

    private void loadImage(int resourceId){
        Glide.with(this)
                .load(resourceId)
                .asBitmap()
                .centerCrop()
                .into(new ImageViewTarget<Bitmap>(image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        image.setImageBitmap(resource);
                        initAnimator(getIntent(),image);
                    }
                });
    }


    private void initAnimator(final Intent intent, final AnimatedImageView image) {
        ViewGroup container=ViewGroup.class.cast(image.getParent());
        runner = TransitionRunner.with(intent)
                .target(image)
                .fadeContainer(container)
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
