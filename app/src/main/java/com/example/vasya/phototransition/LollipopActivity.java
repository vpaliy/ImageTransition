package com.example.vasya.phototransition;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

@TargetApi(21)
public class LollipopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestFeature();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }

        final ImageView image=(ImageView) (findViewById(R.id.image));
        final String pathTo=savedInstanceState.getString("key");

        if(pathTo!=null) {
            Glide.with(this)
                    .load(new File(pathTo))
                    .asBitmap()
                    .centerCrop().into(image);
        }
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
        Transition enterTransition=new ChangeBounds();
        enterTransition.setDuration(200);
        enterTransition.setInterpolator(new DecelerateInterpolator());
        getWindow().setSharedElementEnterTransition(enterTransition);
        getWindow().setSharedElementsUseOverlay(false);

    }
}
