package com.vasya.phototransition.prelollipop.eventBus;


import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.vpaliy.transition.ImageState;

public class CallbackRequest {

    private final int scrollTo;
    private final Callback callback;

    public CallbackRequest(int scrollTo, @NonNull Callback callback) {
        this.scrollTo=scrollTo;
        this.callback=callback;
    }

    public int requestedPosition() {
        return scrollTo;
    }

    public void run(ImageState state) {
        callback.onFetched(state);
    }

    public interface Callback {
        void onFetched(ImageState state);
    }
}
