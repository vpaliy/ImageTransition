package com.vasya.phototransition.utils;


import android.content.Context;
import android.util.AttributeSet;

import com.vpaliy.transition.AnimatedImageView;


public class SquareImage extends AnimatedImageView{

    public SquareImage(Context context) {
        this(context, null);
    }

    public SquareImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec,widthMeasureSpec);
    }
}
