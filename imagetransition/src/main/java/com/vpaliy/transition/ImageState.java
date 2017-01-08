package com.vpaliy.transition;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.FloatProperty;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


public class ImageState implements Parcelable {

    private static final String TAG=ImageState.class.getSimpleName();

    private float locationX;
    private float locationY;
    private float width;
    private float height;
    private ImageView.ScaleType scaleType;
    private int imageOrientation;


    public ImageState(Parcel in) {
        this.locationX=in.readFloat();
        this.locationY=in.readFloat();
        this.width=in.readFloat();
        this.height=in.readFloat();
        this.imageOrientation=in.readInt();
        this.scaleType= ImageView.ScaleType.valueOf(in.readString());
    }

    public ImageState(ImageView image) {
        int[] location=new int[2];
        image.getLocationOnScreen(location);
        this.locationX=location[0];
        this.locationY=location[1];
        this.width=image.getWidth();
        this.height=image.getHeight();
        this.scaleType=image.getScaleType();
        this.imageOrientation=image.getContext().getResources().getConfiguration().orientation;
    }

    public ImageState(float locationX, float locationY, int width,
            int height, int orientation, ImageView.ScaleType scaleType) {
        this.locationX=locationX;
        this.locationY=locationY;
        this.width=width;
        this.height=height;
        this.scaleType=scaleType;
        this.imageOrientation=orientation;
    }

    public final static Creator<ImageState>  CREATOR=new Creator<ImageState>() {

        @Override
        public ImageState createFromParcel(Parcel parcel) {
            return new ImageState(parcel);
        }

        @Override
        public ImageState[] newArray(int size) {
            return new ImageState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(locationX);
        parcel.writeFloat(locationY);
        parcel.writeFloat(width);
        parcel.writeFloat(height);
        parcel.writeInt(imageOrientation);
        parcel.writeString(scaleType.name());
    }

    public float locationX() {
        return locationX;
    }

    public float locationY() {
        return locationY;
    }


    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public ImageView.ScaleType scaleType() {
        return scaleType;
    }

    public static ImageState newInstance(ImageView image) {
        return new ImageState(image);
    }

}
