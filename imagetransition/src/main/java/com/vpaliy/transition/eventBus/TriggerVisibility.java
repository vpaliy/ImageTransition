package com.vpaliy.transition.eventBus;

public class TriggerVisibility {

    private final boolean isVisible;
    private final int position;

    public TriggerVisibility(int position, boolean isVisible) {
        this.isVisible = isVisible;
        this.position=position;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int requestedPosition() {
        return position;
    }



}
