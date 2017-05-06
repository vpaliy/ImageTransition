package com.vasya.phototransition.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.vasya.phototransition.R;
import com.vasya.phototransition.utils.Constants;
import android.support.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.annotation.TargetApi;

@TargetApi(21)
public class LollipopActivity extends AppCompatActivity {

    @BindView(R.id.image)
    protected ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }

        int resourceId = savedInstanceState.getInt(Constants.DATA,-1);
        String transitionName = savedInstanceState.getString(Constants.KEY);

        loadImage(resourceId,transitionName);


    }

    private void loadImage(int resourceId, String transitionName){
        postponeEnterTransition();  //postpone the transition
        image.setTransitionName(transitionName);
        Glide.with(this)
                .load(resourceId).asBitmap()
                .centerCrop()
                .into(new ImageViewTarget<Bitmap>(image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        image.setImageBitmap(resource);
                        startTransition(image);
                    }
                });
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

}
