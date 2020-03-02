package com.example.deedsbeeactivitychallenge.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.deedsbeeactivitychallenge.MainActivity;
import com.example.deedsbeeactivitychallenge.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance(String param1, String param2) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private void saveIntValueToSharedPref(String key, int saved_int)
    {
        Context context = getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, saved_int);
        editor.apply();
    }

    private int getIntValueFromSharedPref(String key)
    {
        Context context = getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                key, Context.MODE_PRIVATE);

        return sharedPref.getInt(key, 0);
    }

    private void updateStatistics()
    {

        TextView currentScoreTv = getView().findViewById(R.id.currentScoreResultTextView);
        TextView totalScoreTv = getView().findViewById(R.id.todayScoreResultTextView);
        TextView distanceWalkedTv = getView().findViewById(R.id.distanceWalkedResultTextView);

        //Get current score:
        try
        {

            int currentScore = getIntValueFromSharedPref("currentScore");
            String currentScoreStr = Integer.toString(currentScore);
            currentScoreTv.setText(currentScoreStr);

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
                    saveIntValueToSharedPref("currentScore", 0);
                    saveIntValueToSharedPref("totalScore", 0);
                    saveIntValueToSharedPref("distance", 0);
                    updateStatistics();

                }
            });
            return view;
        }
        catch(NullPointerException e)
        {
            View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
            Log.v("MainActivity", "Fragment 2 not ready yet.");
            return view;
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
