package com.vasya.phototransition.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.vasya.phototransition.R;
import com.vasya.phototransition.utils.Constants;
import com.vpaliy.transition.AnimatedImageView;
import com.vpaliy.transition.ImageState;
import com.vpaliy.transition.TransitionAnimation;
import com.vpaliy.transition.TransitionRunner;
import com.vpaliy.transition.eventBus.CallbackRequest;
import java.util.ArrayList;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;


public class PreLollipopListActivity extends AppCompatActivity {


    private int startPosition;
    private int currentPosition=-1;

    private TransitionRunner runner;
    private ContentSliderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_layout);

        if(savedInstanceState==null)
            savedInstanceState=getIntent().getExtras();
        initUI(savedInstanceState);

    }

    private void initUI(Bundle args) {
        runner=TransitionRunner.with(args);
        ArrayList<Integer> mediaFileList=args.getIntegerArrayList(Constants.DATA);
        startPosition=args.getInt(Constants.START_POSITION);


        ViewPager pager= ButterKnife.findById(this,R.id.slider);
        pager.setAdapter(adapter=new ContentSliderAdapter(mediaFileList));
        pager.setCurrentItem(startPosition);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition= position;
            }
        });

    }

    private CallbackRequest.Callback callback=new CallbackRequest.Callback() {
        @Override
        public void onFetched(ImageState state) {
            if(runner!=null) {
                runner.target(adapter.targetAt(currentPosition))
                        .duration(getResources().getInteger(R.integer.duration))
                        .replace(state)
                        .runAway(TransitionAnimation.EXIT,PreLollipopListActivity.this);
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(runner!=null) {
            if (currentPosition < 0)
                currentPosition = startPosition;
            runner.requestUpdate(currentPosition,callback); //that's it
        }else {
            super.onBackPressed();
        }
    }


    private class ContentSliderAdapter extends PagerAdapter {

        private SparseArray<AnimatedImageView> imageMap=new SparseArray<>();

        private ArrayList<Integer> mediaFileList;
        private boolean hasAnimated=false;
        private LayoutInflater inflater=LayoutInflater.from(PreLollipopListActivity.this);

        public ContentSliderAdapter(ArrayList<Integer> mediaFileList) {
            this.mediaFileList = mediaFileList;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            final AnimatedImageView image=AnimatedImageView.class.cast(inflater.inflate(R.layout.slider_image,container,false));
            container.addView(image);
                Glide.with(image.getContext()).
                        load(mediaFileList.get(position % mediaFileList.size()))
                        .asBitmap()
                        .centerCrop()
                        .into(new ImageViewTarget<Bitmap>(image) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                image.setImageBitmap(resource);
                                if(checkForTransition(position)){
                                    startTransition(image);
                                }

                            }
                        });
            imageMap.put(position,image);
            image.setTag(Constants.TRANSITION_NAME(position));
            return image;
        }

        public AnimatedImageView targetAt(int index) {
            return imageMap.get(index);
        }


        private void startTransition(final AnimatedImageView image) {
            hasAnimated=true;
            if(runner!=null) {
                initAnimator(image);
            }
        }

        private boolean checkForTransition(int position) {
            return !hasAnimated && position==startPosition;
        }

        private void initAnimator(final AnimatedImageView image) {
            ViewGroup container=ViewGroup.class.cast(image.getParent());
            runner.target(image)
                    .fadeContainer(container)
                    .duration(getResources().getInteger(R.integer.duration))
                    .run(TransitionAnimation.ENTER);
        }


        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }
}
