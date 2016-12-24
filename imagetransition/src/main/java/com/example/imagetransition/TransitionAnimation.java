package com.example.imagetransition;

import android.animation.Animator;
import android.os.Build;
import android.view.ViewTreeObserver;

import java.util.List;

public abstract class TransitionAnimation {

    private static final String TAG=TransitionAnimation.class.getSimpleName();

    private TransitionAnimation(){ /*Nothing is going on here */}

    public static final TransitionAnimation ENTER=new TransitionAnimation() {

        @Override
        public void runAnimation(final TransitionRunner.TransitionData data) {
            data.target.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //disable this listener at this point
                    data.target.getViewTreeObserver().removeOnPreDrawListener(this);
                    data.currentState=ImageState.newInstance(data.target);
                    makeTransformation(data);

                    if(Build.VERSION.SDK_INT>=12) {
                        data.target.animate()
                                .scaleX(1.f)
                                .scaleY(1.f)
                                .translationY(0f)
                                .translationX(0f)
                                .setInterpolator(data.interpolator)
                                .setDuration(data.animationDuration)
                                .setListener(provideListener(data.listenerList));
                    }else {
                        //TODO do animation for <12
                    }

                    return true;
                }
            });
        }


    };


    private static Animator.AnimatorListener provideListener(final List<TransitionListener> listenerList) {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(listenerList!=null) {
                    for (TransitionListener listener : listenerList) {
                        listener.onAnimationStart(animator);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(listenerList!=null) {
                    for (TransitionListener listener : listenerList) {
                        listener.onAnimationEnd(animator);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if(listenerList!=null) {
                    for (TransitionListener listener : listenerList) {
                        listener.onAnimationCancel(animator);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if(listenerList!=null) {
                    for (TransitionListener listener : listenerList) {
                        listener.onAnimationRepeat(animator);
                    }
                }
            }
        };
    }

    public static final TransitionAnimation EXIT=new TransitionAnimation() {
        @Override
        public void runAnimation(TransitionRunner.TransitionData data) {

            final float scaleX=data.prevState.width()/data.currentState.width();
            final float scaleY=data.prevState.height()/data.currentState.height();


            final float deltaX=data.prevState.locationX()-data.currentState.locationX();
            final float deltaY=data.prevState.locationY()-data.currentState.locationY();

            if(Build.VERSION.SDK_INT>=12) {
                data.target.animate()
                        .scaleX(scaleX)
                        .scaleY(scaleY)
                        .setInterpolator(data.interpolator)
                        .setDuration(data.animationDuration)
                        .setListener(provideListener(data.listenerList))
                        .translationX(deltaX)
                        .translationY(deltaY);
            }else {
                //TODO do animation for <12
            }



        }
    };


    private static void makeTransformation(TransitionRunner.TransitionData data) {
        final float scaleX=data.prevState.width()/data.currentState.width();
        final float scaleY=data.prevState.height()/data.currentState.height();


        final float deltaX=data.prevState.locationX()-data.currentState.locationX();
        final float deltaY=data.prevState.locationY()-data.currentState.locationY();

        data.target.setPivotX(0);
        data.target.setPivotY(0);

        data.target.setScaleX(scaleX);
        data.target.setScaleY(scaleY);

        data.target.setTranslationX(deltaX);
        data.target.setTranslationY(deltaY);


    }

    public abstract void runAnimation(TransitionRunner.TransitionData data);

}