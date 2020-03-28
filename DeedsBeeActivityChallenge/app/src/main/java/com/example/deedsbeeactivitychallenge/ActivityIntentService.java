package com.example.deedsbeeactivitychallenge;

import java.util.ArrayList;
import java.lang.reflect.Type;
import android.content.Context;

import com.example.deedsbeeactivitychallenge.ui.main.Constants;
import com.google.gson.Gson;
import android.content.Intent;
import android.app.IntentService;
import android.preference.PreferenceManager;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//REFERENCE: https://www.androidhive.info/2017/12/android-user-activity-recognition-still-walking-running-driving-etc/

//Extend IntentService//
public class ActivityIntentService extends IntentService {
    protected static final String TAG = "Activity";
    //Call the super IntentService constructor with the name for the worker thread//
    public ActivityIntentService() {
        super(TAG);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    //Define an onHandleIntent() method, which will be called whenever an activity detection update is available//

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
    //Check whether the Intent contains activity recognition data//
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        //Get an array of DetectedActivity objects//
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity activity: detectedActivities)
        {
            broadcastActivity(activity);
        }
    }

    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}