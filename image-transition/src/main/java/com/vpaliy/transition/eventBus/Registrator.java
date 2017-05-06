package com.vpaliy.transition.eventBus;

/** This class is designed as utility
 * Register in onStart() method
 * Unregister in onStop() method
 */
public class Registrator {

    public static void register(Object object) {
        EventBusProvider.defaultBus().register(object);
    }

    public static void unregister(Object object) {
        EventBusProvider.defaultBus().unregister(object);
    }
}
