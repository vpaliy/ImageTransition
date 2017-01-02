package com.example.vasya.phototransition.lollipop;

import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TargetApi(21)
public class ListLollipopActivity extends AppCompatActivity {

    private static final String TAG=ListLollipopActivity.class.getSimpleName();

    private ArrayList<Integer> mediaFileList;
    private ViewPager pager;
    private boolean mIsReturning=false;
    private int startPosition;
    private int currentPosition=-1;
    private boolean isPicasso;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestFeature();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_layout);
        postponeEnterTransition();
        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }
        initUI(savedInstanceState);

    }

    private void initUI(Bundle args) {

        isPicasso=args.getBoolean(ProjectUtils.PICASSO);
        mediaFileList=args.getIntegerArrayList(ProjectUtils.DATA);
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
    public void finishAfterTransition() {
        if(currentPosition>0) {
            mIsReturning = true;
            Intent data = new Intent();
            data.putExtra(ProjectUtils.START_POSITION, startPosition);
            data.putExtra(ProjectUtils.CURRENT_POSITION, currentPosition);
            setResult(RESULT_OK, data);
        }
        super.finishAfterTransition();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ProjectUtils.DATA,mediaFileList);
        outState.putInt(ProjectUtils.START_POSITION,startPosition);
        outState.putInt(ProjectUtils.CURRENT_POSITION,currentPosition);
        outState.putBoolean(ProjectUtils.PICASSO,isPicasso);
    }

    private class ContentSliderAdapter extends PagerAdapter {

        private ArrayList<Integer> mediaFileList;
        private boolean hasAnimated=false;
        private LayoutInflater inflater=LayoutInflater.from(ListLollipopActivity.this);

        public ContentSliderAdapter(ArrayList<Integer> mediaFileList) {
            this.mediaFileList = mediaFileList;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            //TODO fix the inflation here
            final ImageView itemView=(ImageView)inflater.inflate(R.layout.slider_image,container,false);
            container.addView(itemView);
            itemView.setTransitionName(ProjectUtils.TRANSITION_NAME(position));
            if(isPicasso) {
                Picasso.with(itemView.getContext())
                    .load(mediaFileList.get(position % mediaFileList.size()))
                    .fit().centerCrop().into(itemView,
                        checkForTransition(position)? new Callback() {
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
            image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    image.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }

        private boolean checkForTransition(int position) {
            if(position==startPosition) {
                if (!hasAnimated) {
                    return (hasAnimated = true);
                }
            }
            return false;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
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

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mIsReturning) {
                    View sharedElement =pager.findViewWithTag(ProjectUtils.TRANSITION_NAME(currentPosition));
                    if (startPosition != currentPosition) {
                        names.clear();
                        names.add(sharedElement.getTransitionName());
                        sharedElements.clear();
                        sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                    }
                }
            }
        });
    }
}
