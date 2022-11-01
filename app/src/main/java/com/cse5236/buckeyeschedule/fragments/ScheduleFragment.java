package com.cse5236.buckeyeschedule.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentAccountBinding;
import com.cse5236.buckeyeschedule.databinding.FragmentScheduleBinding;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("My Schedule");
        ((MainActivity)getActivity()).miniIconHelper(true);
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        setListeners();
        Log.d("fragment lifecycle","ScheduleFragment onCreateView invoked");
        return v;
    }

    private void setListeners() {
        binding.imageAddScheduleButton.setOnClickListener(v -> {
            ((MainActivity)getActivity()).replaceFragment(new CreateScheduleFragment(), "Create Schedule");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","ScheduleFragment onDestroyView invoked");
    }
}