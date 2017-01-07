package com.vasya.phototransition;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.vasya.phototransition.lollipop.ListLollipopActivity;
import com.vasya.phototransition.lollipop.LollipopActivity;
import com.vasya.phototransition.prelollipop.PreLollipopActivity;
import com.vasya.phototransition.prelollipop.PreLollipopListActivity;
import com.vasya.phototransition.prelollipop.eventBus.CallbackRequest;
import com.vasya.phototransition.prelollipop.eventBus.EventBusProvider;
import com.vasya.phototransition.prelollipop.eventBus.TriggerVisibility;
import com.vasya.phototransition.utils.DynamicImage;
import com.vasya.phototransition.utils.ProjectUtils;
import com.vpaliy.transition.ImageState;
import com.vpaliy.transition.TransitionStarter;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG=MainActivity.class.getSimpleName();
    private static boolean DEBUG=false;

    private static final int PRE_LOLLIPOP=0;
    private static final int LOLLIPOP=1;
    private static final int PRE_LOLLIPOP_SLIDER=2;
    private static final int LOLLIPOP_SLIDER=3;

    private int transitionChoice=LOLLIPOP;
    private Bundle reenterState;
    private RecyclerView recyclerView;
    private boolean isPicasso=false;

    private ImageView currentSharedImage;

    private Bus bus= EventBusProvider.defaultBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            requestFeature();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initActionBar();
    }

    private void initActionBar() {
        if(getSupportActionBar()==null) {
            setSupportActionBar((Toolbar)(findViewById(R.id.actionBar)));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    private void initUI() {


        List<Integer> rawDrawableList= Arrays.asList(R.drawable.eleven, R.drawable.fifteen, R.drawable.five,
                R.drawable.four, R.drawable.fourteen, R.drawable.seven, R.drawable.seventeen,
                R.drawable.six, R.drawable.sixteen, R.drawable.ten, R.drawable.thirt, R.drawable.three, R.drawable.twelve,
                R.drawable.two);

        recyclerView=(RecyclerView)(findViewById(R.id.recyclerView));
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,
                getResources().getInteger(R.integer.gridSpanSize),GridLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new GalleryAdapter(MainActivity.this,new ArrayList<>(rawDrawableList)));
        final DrawerLayout layout=(DrawerLayout)(findViewById(R.id.drawerLayout));
        NavigationView navigationView=(NavigationView)(findViewById(R.id.navigation));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.lollipop:
                        transitionChoice=LOLLIPOP;
                        break;
                    case R.id.pre_lollipop:
                        transitionChoice=PRE_LOLLIPOP;
                        break;
                    case R.id.lollipop_slider:
                        transitionChoice=LOLLIPOP_SLIDER;
                        break;
                    case R.id.pre_lollipop_slider:
                        transitionChoice=PRE_LOLLIPOP_SLIDER;
                        break;
                }
                layout.closeDrawers();
                return true;
            }
        });


    }

    @Override
    @TargetApi(21)
    public void onActivityReenter(final int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if(transitionChoice==LOLLIPOP_SLIDER) {
            reenterState = data.getExtras();
            int startPosition = reenterState.getInt(ProjectUtils.START_POSITION);
            final int currentPosition = reenterState.getInt(ProjectUtils.CURRENT_POSITION);

            //scroll to the image
            if (startPosition != currentPosition) {
                recyclerView.scrollToPosition(currentPosition);
            }

            //if the user has reached an image which is beyond the screen,
            // we need to apply code below, in order to shift the RecyclerView up/down and create a transition
            postponeEnterTransition();

            recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    recyclerView.requestLayout();
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.loader,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.picasso:
                isPicasso=true;
                return true;
            case R.id.glide:
                isPicasso=false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void triggerVisibility(TriggerVisibility trigger) {
        ImageView image=(ImageView)(recyclerView.
                findViewWithTag(ProjectUtils.TRANSITION_NAME(trigger.requestedPosition())));
        image.setVisibility(trigger.isVisible()?View.VISIBLE:View.INVISIBLE);
    }


    @Subscribe
    public void requestForData(final CallbackRequest request) {
        recyclerView.scrollToPosition(request.requestedPosition());
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                recyclerView.requestLayout();
                ImageView image=(ImageView)(recyclerView.findViewWithTag
                (ProjectUtils.TRANSITION_NAME(request.requestedPosition())));
                request.run(ImageState.newInstance(image));
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    private class GalleryAdapter extends
            RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

        private ArrayList<Integer> imageList;
        private LayoutInflater inflater;

        public GalleryAdapter(Context context, ArrayList<Integer> imageList) {
            this.inflater=LayoutInflater.from(context);
            this.imageList=imageList;
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            private DynamicImage image;

            public ImageViewHolder(View itemView) {
                super(itemView);
                image=(DynamicImage)(itemView);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (transitionChoice) {
                            case LOLLIPOP:
                            case PRE_LOLLIPOP: {
                                launchDetailsActivity(imageList.get(getAdapterPosition() % imageList.size()),
                                        image,getAdapterPosition());
                                break;
                            }
                            case LOLLIPOP_SLIDER:
                            case PRE_LOLLIPOP_SLIDER: {
                                launchSliderActivity(imageList,image,getAdapterPosition());
                                break;
                            }
                        }
                    }
                });
            }

            public void onBindData() {
                Glide.with(itemView.getContext())
                        .load(imageList.get(getAdapterPosition()%imageList.size()))
                        .asBitmap()
                        .centerCrop()
                        .into(image);
                image.setTag(ProjectUtils.TRANSITION_NAME(getAdapterPosition()));
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    image.setTransitionName(ProjectUtils.TRANSITION_NAME(getAdapterPosition()));
                }
            }
        }



        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root=inflater.inflate(R.layout.image,parent,false);
            return new ImageViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.onBindData();
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }



    private void launchSliderActivity(ArrayList<Integer> mediaFileList, ImageView image, int position) {
        if(transitionChoice==LOLLIPOP_SLIDER) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(this, ListLollipopActivity.class);
                intent.putExtra(ProjectUtils.DATA, mediaFileList);
                intent.putExtra(ProjectUtils.START_POSITION, position);
                intent.putExtra(ProjectUtils.PICASSO,isPicasso);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this, image, image.getTransitionName());
                startActivity(intent, options.toBundle());
            }else {
                Toast.makeText(this,"Current device doesn't support Lollipop version",Toast.LENGTH_LONG).show();
            }
        }else {
            Intent intent=new Intent(this, PreLollipopListActivity.class);
            intent.putExtra(ProjectUtils.DATA, mediaFileList);
            intent.putExtra(ProjectUtils.START_POSITION, position);
            intent.putExtra(ProjectUtils.PICASSO,isPicasso);
            TransitionStarter.with(this).from(image).startForResult(intent,1);
        }

    }


    private void launchDetailsActivity(int resourceId,ImageView image, int position) {
        if(transitionChoice==PRE_LOLLIPOP) {
            Intent intent=new Intent(this, PreLollipopActivity.class);
            intent.putExtra(ProjectUtils.DATA,resourceId);
            intent.putExtra(ProjectUtils.PICASSO,isPicasso);
            intent.putExtra(ProjectUtils.START_POSITION,position);
            TransitionStarter.with(this).from(image).start(intent); //that's it!
        }else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                Intent intent=new Intent(this, LollipopActivity.class);
                intent.putExtra(ProjectUtils.KEY,image.getTransitionName());
                intent.putExtra(ProjectUtils.DATA,resourceId);
                intent.putExtra(ProjectUtils.PICASSO,isPicasso);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, image, image.getTransitionName());
                startActivity(intent, options.toBundle());
            }else {
                Toast.makeText(this,"Current device doesn't support Lollipop version",Toast.LENGTH_LONG).show();
            }
        }
    }


    @TargetApi(21)
    private void requestFeature() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementsUseOverlay(false);

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if(reenterState!=null) {
                    int startPosition = reenterState.getInt(ProjectUtils.START_POSITION);
                    int currentPosition = reenterState.getInt(ProjectUtils.CURRENT_POSITION);

                    if(startPosition!=currentPosition) {
                        View sharedElement=recyclerView.findViewWithTag(ProjectUtils.TRANSITION_NAME(currentPosition));
                        names.clear();
                        sharedElements.clear();
                        names.add(ProjectUtils.TRANSITION_NAME(currentPosition));
                        sharedElements.put(ProjectUtils.TRANSITION_NAME(currentPosition),sharedElement);
                    }
                    reenterState=null;

                }else {
                    View navigationBar = findViewById(android.R.id.navigationBarBackground);
                    View statusBar = findViewById(android.R.id.statusBarBackground);
                    if (navigationBar != null) {
                        names.add(navigationBar.getTransitionName());
                        sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                    }
                    if (statusBar != null) {
                        names.add(statusBar.getTransitionName());
                        sharedElements.put(statusBar.getTransitionName(), statusBar);
                    }
                }
            }
        });
    }
}
