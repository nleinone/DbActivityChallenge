package com.example.deedsbeeactivitychallenge;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.example.deedsbeeactivitychallenge.ui.main.BackgroundDetectedActivitiesService;
import com.example.deedsbeeactivitychallenge.ui.main.Constants;
import com.example.deedsbeeactivitychallenge.ui.main.Fragment1;
import com.example.deedsbeeactivitychallenge.ui.main.Fragment2;
import com.google.android.material.tabs.TabLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.deedsbeeactivitychallenge.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener {

    BroadcastReceiver broadcastReceiver;
    private TextView txtActivity, txtConfidence;

    private int[] icons = {
            R.drawable.star,
            R.drawable.pie,
    };

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Activity recognition:
        txtActivity = findViewById(R.id.activityTv);
        txtConfidence = findViewById(R.id.confTv);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);

        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(icons[0]);
        tabs.getTabAt(1).setIcon(icons[1]);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };
        startTracking();
        updateStatistics();

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
        updateStatistics();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.unknown_activity);

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.vehicle);
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.bicycle);
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.foot);
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.running);
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.still);
                break;
            }
            case DetectedActivity.TILTING: {
                label = getString(R.string.tilting);
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.walking);
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.unknown_activity);
                break;
            }
        }

        txtActivity = findViewById(R.id.activityTv);
        txtConfidence = findViewById(R.id.confTv);

        if (confidence > Constants.CONFIDENCE) {
            try
            {
                Log.v("MainActivity", "User activity: " + label + ", Confidence: " + confidence);
                txtActivity.setText(label);
                String text = "Confidence: " + confidence;
                updateScore(label);
                txtConfidence.setText(text);
                updateStatistics();
            }
            catch(Exception e)
            {
                Log.v("MainActivity e: ", e.toString());
                Log.v("MainActivity ERROR", "User activity: " + label + ", Confidence: " + confidence);
            }
        }
    }

    private void saveIntValueToSharedPref(String key, int saved_int)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, saved_int);
        editor.apply();
    }

    private int getIntValueFromSharedPref(String key)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        return sharedPref.getInt(key, 0);
    }

    private void updateStatistics()
    {

        TextView currentScoreTv = findViewById(R.id.currentScoreResultTextView);
        TextView totalScoreTv = findViewById(R.id.todayScoreResultTextView);
        TextView distanceWalkedTv = findViewById(R.id.distanceWalkedResultTextView);
        TextView currentScoreCircleTv = findViewById(R.id.scoreTextView);
        //Get current score:
        try
        {

            int currentScore = getIntValueFromSharedPref("currentScore");
            String currentScoreStr = Integer.toString(currentScore);
            currentScoreTv.setText(currentScoreStr);

            currentScoreCircleTv.setText(currentScoreStr);

            int totalScore = getIntValueFromSharedPref("totalScore");
            String totalScoreStr = Integer.toString(currentScore);
            totalScoreTv.setText(totalScoreStr);

            int distance = getIntValueFromSharedPref("distance");
            String distanceStr = Integer.toString(distance);
            distanceWalkedTv.setText(distanceStr);

            Log.e("MainActivity", "currentScore: " + currentScoreStr);
            Log.e("MainActivity", "totalScore: " + totalScoreStr);
            Log.e("MainActivity", "dist: " + distanceStr);
        }
        catch(NullPointerException e)
        {
            Log.e("MainActivity", e.toString());
            //totalScoreTv.setText("0");
            //distanceWalkedTv.setText("0");
            //currentScoreTv.setText("0");
        }

    }

    private void startTracking() {
        Log.v("MainActivity", "startTracking");
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void edit_score(int current_score_int, TextView scoreTv, Boolean isPenalty, int pointModifier)
    {
        String key = "currentScore";
        if(isPenalty)
        {
            current_score_int = current_score_int - pointModifier;
            String current_score_string = Integer.toString(current_score_int);
            scoreTv.setText(current_score_string);
            saveIntValueToSharedPref(key, current_score_int);
            updateScoreColor(scoreTv);
        }
        else
        {
            current_score_int = current_score_int + pointModifier;
            String current_score_string = Integer.toString(current_score_int);
            scoreTv.setText(current_score_string);
            //Save current score
            saveIntValueToSharedPref(key, current_score_int);

            //Save total score
            int totalScore = getIntValueFromSharedPref("totalScore");
            totalScore = totalScore + current_score_int;
            saveIntValueToSharedPref("totalScore", totalScore);

            updateScoreColor(scoreTv);
        }
    }

    private void updateScoreColor(TextView scoreTv)
    {
        String current_score_string = scoreTv.getText().toString();
        Log.v("MainActivity", "current_score_string 2: " + current_score_string);
        int current_score_int = Integer.parseInt(current_score_string);

        Log.v("MainActivity", "current_score_int 2: " + current_score_int);

        if(current_score_int < 100)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color1));
        }
        else if(current_score_int < 200)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color2));
        }
        else if(current_score_int < 300)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color3));
        }
        else if(current_score_int < 400)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color4));
        }
        else if(current_score_int < 500)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color5));
        }
        else if(current_score_int < 600)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color6));
        }
        else if(current_score_int < 700)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color7));
        }
        else if(current_score_int < 800)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color8));
        }
        else if(current_score_int < 900)
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color9));
        }
        else
        {
            scoreTv.setTextColor(getResources().getColor(R.color.color10));
        }
    }

    private void updateScore(String activity_status)
    {

        TextView scoreTv = findViewById(R.id.scoreTextView);
        String current_score_string = scoreTv.getText().toString();
        int current_score_int = Integer.parseInt(current_score_string);

        int still_penalty = Constants.PENALTY_SCORE;
        int walking_score = Constants.WALKING_SCORE;
        int running_score = Constants.RUNNING_SCORE;

        Log.v("MainActivity", "activity_status: " + activity_status);

        //User will lose points when still.
        if(activity_status.equals(getString(R.string.still)))
        {
            Boolean isPenalty = true;
            Log.v("MainActivity", "current_score_int: " + current_score_int);
            if(current_score_int > 0)
            {
                Log.v("MainActivity", "current_score_int: " + current_score_string);
                edit_score(current_score_int, scoreTv, isPenalty, still_penalty);
            }
            else
            {
                updateScoreColor(scoreTv);
            }
        }

        //User will gain points when walking.
        else if(activity_status.equals(getString(R.string.walking)))
        {
            if (current_score_int < Constants.MAX_SCORE)
            {
                Boolean isPenalty = false;
                edit_score(current_score_int, scoreTv, isPenalty, walking_score);
            }

        }

        //User will lose points when on foot.
        else if(activity_status.equals(getString(R.string.foot)))
        {
            if(current_score_int < Constants.MAX_SCORE)
            {
                Boolean isPenalty = false;
                edit_score(current_score_int, scoreTv, isPenalty, walking_score);
            }
        }

        //User will lose points when running.
        else if(activity_status.equals(getString(R.string.running)))
        {
            if(current_score_int < Constants.MAX_SCORE)
            {
                Boolean isPenalty = false;
                edit_score(current_score_int, scoreTv, isPenalty, running_score);
            }
        }

        //User will lose points when cycling.
        else if(activity_status.equals(getString(R.string.bicycle)))
        {
            if(current_score_int < Constants.MAX_SCORE)
            {
                Boolean isPenalty = false;
                edit_score(current_score_int, scoreTv, isPenalty, running_score);
            }

        }

        //User will lose points when in vehicle
        else
        {
            if(current_score_int > Constants.MIN_SCORE) {

                Boolean isPenalty = true;
                edit_score(current_score_int, scoreTv, isPenalty, still_penalty);
            }
        }
    }
}