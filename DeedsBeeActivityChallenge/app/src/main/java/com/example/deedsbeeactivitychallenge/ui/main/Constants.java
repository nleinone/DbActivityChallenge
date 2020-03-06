package com.example.deedsbeeactivitychallenge.ui.main;

//REFERENCE: https://www.androidhive.info/2017/12/android-user-activity-recognition-still-walking-running-driving-etc/

public class Constants {

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    static final long DETECTION_INTERVAL_IN_MILLISECONDS = 2 * 5000;

    public static final int CONFIDENCE = 65;

    public static final int WALKING_SCORE = 10;

    public static final int RUNNING_SCORE = 25;

    public static final int PENALTY_SCORE = 1;

    public static final int MAX_SCORE = 10000; //It gets roughly 250 minutes to drain 1k points with 15000 penalty delay.

    public static final int MIN_SCORE = -1000;

    public static final int PENALTY_DELAY = 15000;

}
