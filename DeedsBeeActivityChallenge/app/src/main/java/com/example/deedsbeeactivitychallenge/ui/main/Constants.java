package com.example.deedsbeeactivitychallenge.ui.main;

//REFERENCE: https://www.androidhive.info/2017/12/android-user-activity-recognition-still-walking-running-driving-etc/

public class Constants {

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    static final long DETECTION_INTERVAL_IN_MILLISECONDS = 10 * 1000;

    public static final int CONFIDENCE = 70;

    public static final int WALKING_SCORE = 10;

    public static final int RUNNING_SCORE = 25;

    public static final int PENALTY_SCORE = 1;

    public static final int MAX_SCORE = 1000;

    public static final int MIN_SCORE = 0;
}