package com.vasya.phototransition.activity;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.vasya.phototransition.R;
import com.vasya.phototransition.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;

import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import butterknife.BindView;

@TargetApi(21)
public class ListLollipopActivity extends AppCompatActivity {


    private ArrayList<Integer> mediaFileList;

    @BindView(R.id.slider)
    protected ViewPager pager;

    private boolean isReturning=false;
    private int startPosition;
    private int currentPosition=-1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_layout);
        ButterKnife.bind(this);
        initializeTransition();
        if(savedInstanceState==null) {
            savedInstanceState = getIntent().getExtras();
        }
        setUI(savedInstanceState);

    }


    private void initializeTransition(){
        postponeEnterTransition();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (isReturning) {
                    View sharedElement =pager.findViewWithTag(Constants.TRANSITION_NAME(currentPosition));
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

    private void setUI(Bundle args) {
        mediaFileList=args.getIntegerArrayList(Constants.DATA);
        startPosition=args.getInt(Constants.START_POSITION);
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
            isReturning = true;
            Intent data = new Intent();
            data.putExtra(Constants.START_POSITION, startPosition);
            data.putExtra(Constants.CURRENT_POSITION, currentPosition);
            setResult(RESULT_OK, data);
        }
        super.finishAfterTransition();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.DATA,mediaFileList);
        outState.putInt(Constants.START_POSITION,startPosition);
        outState.putInt(Constants.CURRENT_POSITION,currentPosition);
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
            final ImageView image=ImageView.class.cast(inflater.inflate(R.layout.slider_image,container,false));
            container.addView(image);
            image.setTransitionName(Constants.TRANSITION_NAME(position));
            Glide.with(image.getContext()).
                    load(mediaFileList.get(position % mediaFileList.size())).
                    asBitmap().centerCrop()
                    .into(new ImageViewTarget<Bitmap>(image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            if(checkForTransition(position)){
                                startTransition(image);
                            }
                        }
                    });
            image.setTag(Constants.TRANSITION_NAME(position));
            return image;
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

}
