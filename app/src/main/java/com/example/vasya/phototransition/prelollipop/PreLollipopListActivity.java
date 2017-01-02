package com.example.vasya.phototransition.prelollipop;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.imagetransition.ImageState;
import com.example.imagetransition.TransitionAnimation;
import com.example.imagetransition.TransitionListener;
import com.example.imagetransition.TransitionRunner;
import com.example.imagetransition.TransitionStarter;
import com.example.vasya.phototransition.R;
import com.example.vasya.phototransition.utils.DynamicImage;
import com.example.vasya.phototransition.utils.LoaderCallback;
import com.example.vasya.phototransition.utils.ProjectUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class PreLollipopListActivity extends AppCompatActivity {

    private static final String TAG=PreLollipopListActivity.class.getSimpleName();

    private int startPosition;
    private int currentPosition=-1;
    private ViewPager pager;
    private TransitionRunner runner;
    private ColorDrawable background=new ColorDrawable(Color.BLACK);
    private ObjectAnimator backgroundAnimator;
    private boolean isPicasso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_layout);
        if(savedInstanceState==null)
            savedInstanceState=getIntent().getExtras();
        initUI(savedInstanceState);

    }

    private void initUI(Bundle args) {
        isPicasso=args.getBoolean(ProjectUtils.PICASSO);
        ImageState prevImageState=args.getParcelable(TransitionStarter.IMAGE_STATE);
        if(prevImageState!=null) {
            runner = TransitionRunner.with(prevImageState);
        }
        ArrayList<Integer> mediaFileList=args.getIntegerArrayList(ProjectUtils.DATA);
        startPosition=args.getInt(ProjectUtils.START_POSITION);

        pager=(ViewPager)(findViewById(R.id.slider));
        pager.setAdapter(new ContentSliderAdapter(mediaFileList));
        pager.setCurrentItem(startPosition);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition= position;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(runner!=null) {
            if(currentPosition<0) {
                runner.addListener(new TransitionListener() {
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
                return;
            }else {
                ImageView newSharedImage=(ImageView)(pager.findViewWithTag(ProjectUtils.TRANSITION_NAME(currentPosition)));
                Intent data=new Intent();
                data.putExtra(ProjectUtils.START_POSITION,startPosition);
                data.putExtra(ProjectUtils.CURRENT_POSITION,currentPosition);
                data.putExtra(TransitionStarter.IMAGE_STATE,ImageState.newInstance(newSharedImage));
                setResult(RESULT_OK,data);
                finish();
            }
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    private class ContentSliderAdapter extends PagerAdapter {

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
            final ImageView itemView=(ImageView)inflater.inflate(R.layout.slider_image,container,false);
            if(isPicasso) {
                Picasso.with(itemView.getContext())
                        .load(mediaFileList.get(position % mediaFileList.size()))
                        .fit().centerCrop().into(itemView,
                        checkForTransition(position)?new Callback() {
                            @Override
                            public void onSuccess() {
                                startTransition(itemView);
                            }

                            @Override
                            public void onError() {
                                startTransition(itemView);
                            }
                        }:null);

            }else {
                Glide.with(itemView.getContext()).
                        load(mediaFileList.get(position % mediaFileList.size())).
                        asBitmap().centerCrop().
                        listener(checkForTransition(position)?new LoaderCallback<Integer, Bitmap>(itemView) {
                            @Override
                            public void onReady(ImageView image) {
                                startTransition(itemView);
                            }
                        }:null).into(itemView);
            }
            itemView.setTag(ProjectUtils.TRANSITION_NAME(position));
            return itemView;
        }

        private void startTransition(final ImageView image) {
            hasAnimated=true;
            if(runner!=null) {
                initAnimator(image);
            }
        }

        private boolean checkForTransition(int position) {
            return !hasAnimated && position==startPosition;
        }

        private void initAnimator(final ImageView image) {
            runner.target(image).addListener(new TransitionListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    ViewGroup parent=(ViewGroup)(image.getParent());
                    parent.setBackgroundDrawable(background);
                    backgroundAnimator= ObjectAnimator.ofInt(background,"alpha",0,255);
                    backgroundAnimator.setDuration(animator.getDuration());
                    backgroundAnimator.setInterpolator(new DecelerateInterpolator());
                    backgroundAnimator.start();
                }
            }).duration(getResources().getInteger(R.integer.duration));
            runner.run(TransitionAnimation.ENTER);
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
