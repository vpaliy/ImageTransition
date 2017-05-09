package com.vpaliy.transition;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.util.Arrays;

public class AnimatedImageView extends AppCompatImageView {

    private static final String TAG=AnimatedImageView.class.getSimpleName();

    private ValueAnimator mAnimator;

    private ScaleType originScaleType;
    private long animationDuration;


    public AnimatedImageView(Context context) {
        this(context, null);
    }

    public AnimatedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (originScaleType == null) {
            originScaleType = getScaleType();
        }

        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(animationDuration);
    }

    void setInterpolator(TimeInterpolator interpolator) {
        mAnimator.setInterpolator(interpolator);
    }

    void setAnimDuration(long animDuration) {
        this.animationDuration= animDuration;
        mAnimator.setDuration(animationDuration);
    }


    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        originScaleType = scaleType;
    }

    void setTempScaleType(ScaleType scaleType) {
      //  setScaleType(scaleType);
      ///  setFrame(getLeft(), getTop(), getRight(), getBottom());

    }


    Animator animateToScaleType(final ScaleType scaleType) {

      /* super.setScaleType(originScaleType);
        setFrame(getLeft(), getTop(), getRight(), getBottom());
        final Matrix srcMatrix = getImageMatrix();
        final float[] srcValues = new float[9];
        final float[] destValues = new float[9];
        srcMatrix.getValues(srcValues);

        super.setScaleType(scaleType);
        setFrame(getLeft(), getTop(), getRight(), getBottom());
        final Matrix destMatrix = getImageMatrix();
        if (scaleType == ScaleType.FIT_XY) {
            float scaleX = ((float) getWidth()) / getDrawable().getIntrinsicWidth();
            float scaleY = ((float) getHeight()) / getDrawable().getIntrinsicHeight();
            destMatrix.postScale(scaleX, scaleY);
        }
        destMatrix.getValues(destValues);


        final float transX = destValues[Matrix.MTRANS_X] - srcValues[Matrix.MTRANS_X];
        final float transY = destValues[Matrix.MTRANS_Y] - srcValues[Matrix.MTRANS_Y];
        final float scaleX = destValues[Matrix.MSCALE_X] - srcValues[Matrix.MSCALE_X];
        final float scaleY = destValues[Matrix.MSCALE_Y] - srcValues[Matrix.MSCALE_Y];
        final float skewX=destValues[Matrix.MSKEW_X]-srcValues[Matrix.MSKEW_X];
        final float skewY=destValues[Matrix.MSKEW_Y]-srcValues[Matrix.MSKEW_Y];
        final float pres0=destValues[Matrix.MPERSP_0]-srcValues[Matrix.MPERSP_0];
        final float pres1=destValues[Matrix.MPERSP_1]-srcValues[Matrix.MPERSP_1];
        final float pres2=destValues[Matrix.MPERSP_2]-srcValues[Matrix.MPERSP_2];

        super.setScaleType(ScaleType.MATRIX);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                final float[] currValues = Arrays.copyOf(srcValues, srcValues.length);

                currValues[Matrix.MTRANS_X] = srcValues[Matrix.MTRANS_X] + transX * value;
                currValues[Matrix.MTRANS_Y] = srcValues[Matrix.MTRANS_Y] + transY * value;
                currValues[Matrix.MSCALE_X] = srcValues[Matrix.MSCALE_X] + scaleX * value;
                currValues[Matrix.MSCALE_Y] = srcValues[Matrix.MSCALE_Y] + scaleY * value;
                currValues[Matrix.MSKEW_X]= srcValues[Matrix.MSKEW_X] + skewX * value;
                currValues[Matrix.MSKEW_Y]= srcValues[Matrix.MSKEW_Y] + skewY * value;
                currValues[Matrix.MPERSP_0] = srcValues[Matrix.MPERSP_0] + pres0 * value;
                currValues[Matrix.MPERSP_1]= srcValues[Matrix.MPERSP_1] + pres1 * value;
                currValues[Matrix.MPERSP_2]= srcValues[Matrix.MPERSP_2] + pres2 * value;

                Drawable drawable = getDrawable();
                drawable.setBounds(
                        0,
                        0,
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());


                Matrix matrix = new Matrix();
                matrix.setValues(currValues);
                setImageMatrix(matrix);
                invalidate();
            }
        });


        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //  setScaleType(scaleType);
            }
        });
            */
        return mAnimator;
    }
}