package com.example.vasya.phototransition;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.imagetransition.ImageState;
import com.example.imagetransition.TransitionAnimation;
import com.example.imagetransition.TransitionListener;
import com.example.imagetransition.TransitionRunner;
import com.example.imagetransition.TransitionStarter;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private TransitionRunner runner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }

        final ImageView image=(ImageView) (findViewById(R.id.image));

        String pathTo=savedInstanceState.getString("key");

        final Bundle bundle=savedInstanceState;
        if(pathTo!=null) {
            Glide.with(this)
                    .load(new File(pathTo))
                    .asBitmap()
                    .thumbnail(0.2f)    //to make the loading of image faster
                    .centerCrop()
                    //to prevent the animation from shuddering, use the listener to track when the image is ready,
                    // otherwise you may start animation when the resource hasn't been loaded yet
                    .listener(new RequestListener<File, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, File model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        //when the image has been loaded, start transition
                        @Override
                        public boolean onResourceReady(Bitmap resource, File model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if(isFirstResource) {
                                ImageState passedImage = bundle.getParcelable(TransitionStarter.IMAGE_STATE);
                                if (passedImage != null) {
                                    runner = TransitionRunner.with(passedImage).target(image);
                                    runner.run(TransitionAnimation.ENTER);
                                }
                            }
                            return false;
                        }
                    })
                    .into(image);
        }
    }

    @Override
    public void onBackPressed() {
        //if the animation has occurred, go ahead and start animating the image backwards
        if(runner!=null) {
            runner.addListener(new TransitionListener() {
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
}
