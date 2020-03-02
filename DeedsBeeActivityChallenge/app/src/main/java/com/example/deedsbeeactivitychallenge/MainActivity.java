package com.example.deedsbeeactivitychallenge;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.deedsbeeactivitychallenge.ui.main.BackgroundDetectedActivitiesService;
import com.example.deedsbeeactivitychallenge.ui.main.Constants;
import com.example.deedsbeeactivitychallenge.ui.main.Fragment1;
import com.example.deedsbeeactivitychallenge.ui.main.Fragment2;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.content.SharedPreferences;
import android.app.PendingIntent;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.deedsbeeactivitychallenge.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener {


    //Activity recognition:
    private Context mContext;
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    BroadcastReceiver broadcastReceiver;
    private TextView txtActivity, txtConfidence;
    //Define an ActivityRecognitionClient//

    SharedPreferences.OnSharedPreferenceChangeListener prefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs,
                                                      String key) {
                    if (key.equals(DETECTED_ACTIVITY)) {
                    }
                }
            };

    private ActivityRecognitionClient mActivityRecognitionClient;

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

        mContext = this;

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
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));

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

        Log.e("MainActivity", "User activity: " + label + ", Confidence: " + confidence);

        txtActivity = findViewById(R.id.activityTv);
        txtConfidence = findViewById(R.id.confTv);

        if (confidence > Constants.CONFIDENCE) {
            try
            {
                Log.e("MainActivity", "User activity: " + label + ", Confidence: " + confidence);
                txtActivity.setText(label);
                String text = "Confidence: " + confidence;
                txtConfidence.setText(text);
            }
            catch(Exception e)
            {
                Log.e("MainActivity", "User activity: " + label + ", Confidence: " + confidence);
            }
        }
    }

    private void startTracking() {
        Log.v("MainActivity", "startTracking");
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

}