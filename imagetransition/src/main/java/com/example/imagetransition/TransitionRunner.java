package com.example.imagetransition;

import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransitionRunner {

    private static final String TAG=TransitionRunner.class.getSimpleName();
    private final TransitionData data=new TransitionData();

    static class TransitionData {

        static final long DEFAULT_DURATION=400L;

        ImageState prevState;
        ImageState currentState;
        ImageView target;

        long animationDuration=DEFAULT_DURATION;

        TimeInterpolator interpolator=new DecelerateInterpolator();
        List<TransitionListener> listenerList;

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

    public void run(TransitionAnimation animationInstance) {
        animationInstance.runAnimation(data);
    }

}
