package com.example.deedsbeeactivitychallenge.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.AlertDialog;


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
            final Context context = getContext();
            //REF: https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
            resetBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    new AlertDialog.Builder(context)
                            .setTitle("RESET ALL STATS AND START OVER?")
                            .setMessage("Are you sure you want to RESET and Start the challenge over?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    mListener.saveIntValueToSharedPref("currentScore", 0);
                                    mListener.saveIntValueToSharedPref("totalScore", 0);
                                    mListener.saveIntValueToSharedPref("distance", 0);
                                    mListener.updateTextViews();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
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
        void updateTextViews();
    }
}
