package com.cse5236.buckeyeschedule.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;

public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("My Schedule");
        // Inflate the layout for this fragment
        // Toast.makeText(getActivity(), "Schedule Fragment", Toast.LENGTH_SHORT).show();
        Log.d("fragment lifecycle","ScheduleFragment onCreateView invoked");
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","ScheduleFragment onDestroyView invoked");
    }
}