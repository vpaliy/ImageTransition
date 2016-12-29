package com.example.imagetransition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransitionRunner {

    private static final String TAG=TransitionRunner.class.getSimpleName();
    private final TransitionData data=new TransitionData();

    static class TransitionData {

        static final int RUNNING = 1;
        static final int FINISHED = -1;

        int animationState = FINISHED;

        static final long DEFAULT_DURATION = 400L;

        ImageState prevState;
        ImageState currentState;
        ImageView target;

        long animationDuration = DEFAULT_DURATION;

        TimeInterpolator interpolator = new DecelerateInterpolator();
        List<TransitionListener> listenerList = new ArrayList<>();
        {
            listenerList.add(new TransitionListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    animationState=RUNNING;
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
        data.prevState=prevState;
    }

    public static TransitionRunner with(@NonNull ImageState prevState) {
        return new TransitionRunner(prevState);
    }

    public TransitionRunner target(@NonNull ImageView target) {
        data.target=target;
        data.currentState=ImageState.newInstance(target);
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
        if(data.animationState==TransitionData.RUNNING) {
            data.target.animate().cancel();
        }
    }

    public void run(TransitionAnimation animationInstance) {
        if(isRunning()) {
            cancel();
        }
        animationInstance.runAnimation(data);
    }
}
