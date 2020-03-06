package com.example.deedsbeeactivitychallenge;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import android.widget.TextView;

import android.os.Handler;

import com.example.deedsbeeactivitychallenge.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener {

    BroadcastReceiver broadcastReceiver;
    private TextView txtActivity, txtConfidence;

    private int[] icons = {
            R.drawable.star,
            R.drawable.pie,
    };

    //Timer for penalty: REF: https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        calculateScoreFromTime();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed( runnable = new Runnable() {
            public void run() {

                updateScore();
                updateTextViews();
                checkWinningCondition();
                handler.postDelayed(runnable, Constants.PENALTY_DELAY);
            }
        }, Constants.PENALTY_DELAY);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed( runnable = new Runnable() {
            public void run() {

                updateScore();
                updateTextViews();
                checkWinningCondition();

                handler.postDelayed(runnable, Constants.PENALTY_DELAY);
            }
        }, Constants.PENALTY_DELAY);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveTimeToPrefs();
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


                txtConfidence.setText(text);

                //updateScore(label);
                //updateStatistics();

                saveStrValueToSharedPref("activityStatus", label);
            }
            catch(Exception e)
            {
                Log.v("MainActivity e: ", e.toString());
                Log.v("MainActivity ERROR", "User activity: " + label + ", Confidence: " + confidence);
            }
        }
    }

    public void saveIntValueToSharedPref(String key, int saved_int)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, saved_int);
        editor.apply();
    }

    public void saveStrValueToSharedPref(String key, String saved_str)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, saved_str);
        editor.apply();
    }

    public int getIntValueFromSharedPref(String key)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        return sharedPref.getInt(key, 0);
    }

    public String getStrValueToSharedPref(String key)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        return sharedPref.getString(key, "0");
    }

    private void startTracking() {
        Log.v("MainActivity", "startTracking");
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void saveTimeToPrefs()
    {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        Log.e("MainActivity", "Time: " + ts);
        saveStrValueToSharedPref("currentTime", ts);
    }

    private void calculateScoreFromTime()
    {
        String savedTimeStr = getStrValueToSharedPref("currentTime");
        int savedTimeInt = Integer.parseInt(savedTimeStr);
        Log.e("MainActivity", "savedTimeInt: " + savedTimeInt);

        if(savedTimeInt != 0)
        {
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            int currentTimeInt = Integer.parseInt(ts);

            int timeDifference = currentTimeInt - savedTimeInt;

            Log.e("MainActivity", "currentTimeInt: " + currentTimeInt);
            Log.e("MainActivity", "savedTimeInt: " + savedTimeInt);
            Log.e("MainActivity", "Time diff: " + timeDifference);

            //Time difference affects to the score the following way:
            //Constants.PENALTY_DELAY = N / 1000 (seconds)
            //Given penalty to the current score = timeDifference (seconds) / N

            int currentScore = getIntValueFromSharedPref("currentScore");
            int penaltyScore = timeDifference / (Constants.PENALTY_DELAY / 1000);
            Log.e("MainActivity", "(Constants.PENALTY_DELAY / 1000): " + (Constants.PENALTY_DELAY / 1000));
            Log.e("MainActivity", "penaltyScore: " + penaltyScore);
            int newScore = currentScore - penaltyScore;
            saveIntValueToSharedPref("currentScore", newScore);
        }

    }

    private void checkWinningCondition()
    {
        int score = getIntValueFromSharedPref("currentScore");
        if(score == Constants.MIN_SCORE)
        {
            //You lost
            Context context = getApplicationContext();
            new AlertDialog.Builder(context)
                    .setTitle("You lost the challenge :(")
                    .setMessage("Don't give up! Try again!")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Log.v("MainActivity", "Scores reset");
                            saveIntValueToSharedPref("currentScore", 0);
                            saveIntValueToSharedPref("totalScore", 0);
                            saveIntValueToSharedPref("distance", 0);
                            updateTextViews();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Log.v("MainActivity", "Scores reset");
            saveIntValueToSharedPref("currentScore", 0);
            saveIntValueToSharedPref("totalScore", 0);
            saveIntValueToSharedPref("distance", 0);
            updateTextViews();
        }
        if(score == Constants.MAX_SCORE)
        {
            //You won
            Context context = getApplicationContext();
            new AlertDialog.Builder(context)
                    .setTitle("You WON the challenge!!!")
                    .setMessage("Congratulations! You have beaten the Deedsbee Activity challenge! Take a screenshot of your accomplishment and encourage your friends to do the same")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Log.v("MainActivity", "Scores reset");
                            saveIntValueToSharedPref("currentScore", 0);
                            saveIntValueToSharedPref("totalScore", 0);
                            saveIntValueToSharedPref("distance", 0);
                            updateTextViews();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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

    public void updateTextViews()
    {
        TextView currentScoreTv = findViewById(R.id.currentScoreResultTextView);
        TextView totalScoreTv = findViewById(R.id.todayScoreResultTextView);
        TextView distanceWalkedTv = findViewById(R.id.distanceWalkedResultTextView);
        TextView currentScoreCircleTv = findViewById(R.id.scoreTextView);

        int currentScore = getIntValueFromSharedPref("currentScore");
        int totalScore = getIntValueFromSharedPref("totalScore");

        try
        {
            String currentScoreStr = Integer.toString(currentScore);
            currentScoreTv.setText(currentScoreStr);
            currentScoreCircleTv.setText(currentScoreStr);

            String totalScoreStr = Integer.toString(totalScore);
            totalScoreTv.setText(totalScoreStr);

            int distance = getIntValueFromSharedPref("distance");
            String distanceStr = Integer.toString(distance);
            distanceWalkedTv.setText(distanceStr);

            updateScoreColor(currentScoreCircleTv);

            Log.e("MainActivity", "currentScore: " + currentScoreStr);
            Log.e("MainActivity", "totalScore: " + totalScoreStr);
            Log.e("MainActivity", "dist: " + distanceStr);
            String activity = getStrValueToSharedPref("currentActivity");
            Log.e("MainActivity", "activity: " + activity);
        }
        catch(NullPointerException e)
        {
            Log.e("MainActivity", e.toString());
        }

    }

    private void calculateAndSaveScoreToPrefs(int current_score_int, Boolean isPenalty, int pointModifier)
    {
        String key = "currentScore";

        if(isPenalty)
        {
            saveStrValueToSharedPref("freezeStatus", "false");
            current_score_int = current_score_int - pointModifier;
            saveIntValueToSharedPref(key, current_score_int);
        }
        else
        {
            saveStrValueToSharedPref("freezeStatus", "false");
            current_score_int = current_score_int + pointModifier;
            //Save current score
            saveIntValueToSharedPref(key, current_score_int);

            //Save total score
            int totalScore = getIntValueFromSharedPref("totalScore");
            totalScore = totalScore + current_score_int;
            saveIntValueToSharedPref("totalScore", totalScore);
        }
    }

    private void updateScore()
    {
        int still_penalty = Constants.PENALTY_SCORE;
        int walking_score = Constants.WALKING_SCORE;
        int running_score = Constants.RUNNING_SCORE;
        int doNothing_score = 0;

        String activityStatus = getStrValueToSharedPref("activityStatus");
        Log.v("MainActivity", "statusStrPause: " + activityStatus);

        int currentScore = getIntValueFromSharedPref("currentScore");
        Log.v("MainActivity", "currentScore: " + currentScore);

        //User will lose points when still.
        if(activityStatus.equals(getString(R.string.still)))
        {
            Boolean isPenalty = true;
            if(currentScore > Constants.MIN_SCORE)
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, still_penalty);
            }
            else
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, doNothing_score);
            }
        }

        //User will gain points when walking.
        else if(activityStatus.equals(getString(R.string.walking)))
        {
            Boolean isPenalty = false;
            if (currentScore < Constants.MAX_SCORE)
            {

                calculateAndSaveScoreToPrefs(currentScore, isPenalty, walking_score);
            }
            else
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, doNothing_score);
            }

        }

        //User will lose points when on foot.
        else if(activityStatus.equals(getString(R.string.foot)))
        {
            Boolean isPenalty = false;
            if(currentScore < Constants.MAX_SCORE)
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, walking_score);
            }
            else
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, doNothing_score);
            }
        }

        //User will lose points when running.
        else if(activityStatus.equals(getString(R.string.running)))
        {
            Boolean isPenalty = false;
            if(currentScore < Constants.MAX_SCORE)
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, running_score);
            }
            else
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, doNothing_score);
            }
        }

        //User will lose points when cycling.
        else if(activityStatus.equals(getString(R.string.bicycle)))
        {
            Boolean isPenalty = false;
            if(currentScore < Constants.MAX_SCORE)
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, running_score);
            }
            else
            {
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, doNothing_score);
            }
        }

        //User will lose points when in vehicle
        else
        {
            if(currentScore > Constants.MIN_SCORE) {

                Boolean isPenalty = true;
                calculateAndSaveScoreToPrefs(currentScore, isPenalty, still_penalty);
            }
        }
    }
}