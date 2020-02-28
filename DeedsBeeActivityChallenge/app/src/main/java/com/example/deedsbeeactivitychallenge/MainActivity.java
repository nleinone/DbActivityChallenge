package com.example.deedsbeeactivitychallenge;

import android.net.Uri;
import android.os.Bundle;

import com.example.deedsbeeactivitychallenge.ui.main.Fragment1;
import com.example.deedsbeeactivitychallenge.ui.main.Fragment2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.deedsbeeactivitychallenge.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);

        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(icons[0]);
        tabs.getTabAt(1).setIcon(icons[1]);

    }
}