package com.vasya.phototransition.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.vpaliy.transition.AnimatedImageView;

public class DynamicImage extends AnimatedImageView {

    public DynamicImage(Context context) {
        super(context);
    }

    public DynamicImage(Context context, @NonNull AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       setMeasuredDimension(widthMeasureSpec,widthMeasureSpec);
    }
}
