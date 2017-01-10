package com.vpaliy.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.vpaliy.transition.eventBus.CallbackRequest;
import com.vpaliy.transition.eventBus.EventBusProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransitionRunner {

    private static final String TAG= TransitionRunner.class.getSimpleName();

    private final TransitionData data=new TransitionData();

    static class TransitionData {

        static final int RUNNING = 1;
        static final int FINISHED = -1;
        static final int CANCELED=0;

        int animationState = FINISHED;

        ImageView.ScaleType previousScaleType=null;

        static final long DEFAULT_DURATION = 400L;

        ImageState prevState;
        ImageState currentState;
        AnimatedImageView target;

        int startPosition=-1;

        long animationDuration = DEFAULT_DURATION;

        TimeInterpolator interpolator = new DecelerateInterpolator();
        List<TransitionListener> listenerList = new ArrayList<>();
        {
            listenerList.add(new TransitionListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    if(animationState!=CANCELED) {
                        animationState = RUNNING;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    animationState=FINISHED;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    animationState=FINISHED;
                }
            });
        }
    }


    private TransitionRunner(@NonNull ImageState prevState) {
        this(prevState,-1);
    }


    private TransitionRunner(@NonNull ImageState prevState, int startPosition) {
        data.prevState=prevState;
        data.startPosition=startPosition;
    }

    public TransitionRunner replace(@NonNull ImageState state) {
        if(data.previousScaleType==null) {
            data.previousScaleType = data.prevState.scaleType();
        }
        data.prevState=state;
        return this;
    }

    public static TransitionRunner with(@NonNull Bundle args) {
        ImageState state=args.getParcelable(TransitionStarter.IMAGE_STATE);
        if(state==null) {
            throw new IllegalArgumentException(ImageState.class.getSimpleName() + " is null");
        }
        return new TransitionRunner(state,args.getInt(TransitionStarter.START_POSITION,-1));
    }

     public static TransitionRunner with(@NonNull Intent intent) {
        return with(intent.getExtras());
    }

    public static TransitionRunner with(@NonNull ImageState prevState) {
        return new TransitionRunner(prevState);
    }

    public TransitionRunner target(@NonNull AnimatedImageView target) {
        data.target=target;
        data.target.setAdjustViewBounds(true);
        data.target.setPivotY(0);data.target.setPivotX(0); //may help prevent weird behavior
        data.currentState= ImageState.newInstance(target);
        return this;
    }

    public TransitionRunner duration(long duration) {
        if(duration<0L) {
            throw new IllegalArgumentException("Duration is less than zero");
        }
        data.animationDuration=duration;
        return this;
    }

    public TransitionRunner interpolator(TimeInterpolator interpolator) {
        if(interpolator!=null) {
            data.interpolator = interpolator;
        }
        return this;
    }


    public boolean isRunning() {
        return data.animationState != TransitionData.FINISHED;
    }


    public TransitionRunner clearListeners() {
        data.listenerList=null;
        return this;
    }

    public TransitionRunner addListener(TransitionListener... listeners) {
        if(listeners!=null) {
            if (data.listenerList == null) {
                data.listenerList = new ArrayList<>();
            }
            data.listenerList.addAll(Arrays.asList(listeners));
        }
        return this;
    }

    @TargetApi(14)
    public void cancel() {
        if(data.animationState== TransitionData.RUNNING) {
            data.animationState=TransitionData.CANCELED;
            data.target.animate().cancel();
        }
    }


    public void run(TransitionAnimation animationInstance) {
        if(isRunning()) {
            if(Build.VERSION.SDK_INT<14) {
                return;
            }
            cancel();
        }
        animationInstance.runAnimation(data);
    }

    public void requestUpdate(int position, CallbackRequest.Callback callback) {
        data.startPosition = position;
        EventBusProvider.defaultBus().post(new CallbackRequest(position, callback));
    }

    public void requestUpdate(int position, CallbackRequest.Callback callback, AnimatedImageView target) {
        data.startPosition=position;
        requestUpdate(position,callback);
        target(target);
    }

}
