package com.vasya.phototransition.utils;

public final class ProjectUtils {

    private ProjectUtils() {
        throw new RuntimeException();
    }

    public static final String START_POSITION="st_position";
    public static final String CURRENT_POSITION="current_position";
    public static final String DATA="data";
    public static final String KEY="key";
    public static final String PICASSO="picasso";

    public static String TRANSITION_NAME(int position) {
        return "data:" + Integer.toString(position);
    }


}
