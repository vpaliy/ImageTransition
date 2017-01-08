package com.vpaliy.transition.eventBus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public final class EventBusProvider {

    private static Bus instance;

    public static Bus defaultBus() {
        if (instance == null) {
            synchronized (EventBusProvider.class) {
                if (instance == null) {
                    instance = new Bus(ThreadEnforcer.MAIN);
                }
            }
        }
        return instance;
    }
}
