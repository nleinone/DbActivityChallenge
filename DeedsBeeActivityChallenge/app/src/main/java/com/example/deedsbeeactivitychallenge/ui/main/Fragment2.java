package com.example.deedsbeeactivitychallenge.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.deedsbeeactivitychallenge.R;

public class Fragment2 extends Fragment {

    private OnFragmentInteractionListener mListener;

    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try
        {
            View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
            Button resetBtn = view.findViewById(R.id.resetScoreBtn);
            resetBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.v("MainActivity", "Scores reset");
                    mListener.saveIntValueToSharedPref("currentScore", 0);
                    mListener.saveIntValueToSharedPref("totalScore", 0);
                    mListener.saveIntValueToSharedPref("distance", 0);
                    mListener.updateStatistics();

                }
            });
            return view;
        }
        catch(NullPointerException e)
        {
            View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
            return view;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Interface to herit functions from the mainActivity
    public interface OnFragmentInteractionListener {

        void saveIntValueToSharedPref(String key, int saved_int);
        void saveStrValueToSharedPref(String key, String saved_str);
        int getIntValueFromSharedPref(String key);
        String getStrValueToSharedPref(String key);
        void updateStatistics();
    }
}
