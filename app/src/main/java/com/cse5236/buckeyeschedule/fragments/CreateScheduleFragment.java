package com.cse5236.buckeyeschedule.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentCreateScheduleBinding;
import com.cse5236.buckeyeschedule.databinding.FragmentScheduleBinding;

public class CreateScheduleFragment extends Fragment {

    private FragmentCreateScheduleBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("Create Schedule");
        ((MainActivity)getActivity()).miniIconHelper(false);
        binding = FragmentCreateScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        setListeners();
        Log.d("fragment lifecycle","CreateScheduleFragment onCreateView invoked");
        return v;
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            ((MainActivity)getActivity()).replaceFragment(new ScheduleFragment(), "Schedule");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","CreateScheduleFragment onDestroyView invoked");
    }
}