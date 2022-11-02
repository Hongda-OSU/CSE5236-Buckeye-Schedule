package com.cse5236.buckeyeschedule.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.adapters.ScheduleAdapter;
import com.cse5236.buckeyeschedule.database.ScheduleDatabase;
import com.cse5236.buckeyeschedule.databinding.FragmentAccountBinding;
import com.cse5236.buckeyeschedule.databinding.FragmentScheduleBinding;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private ScheduleAdapter scheduleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("My Schedule");
        ((MainActivity)getActivity()).miniIconHelper(true);
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);

        //scheduleViewModel.deleteAllSchedule();

        // -> is same as onChange overrides
        scheduleViewModel.getAllSchedules.observe(getViewLifecycleOwner(), schedules -> {
            binding.scheduleRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            );
            scheduleAdapter = new ScheduleAdapter(schedules);
            binding.scheduleRecyclerView.setAdapter(scheduleAdapter);
            binding.scheduleRecyclerView.smoothScrollToPosition(0);
        });
        setListeners();
        Log.d("fragment lifecycle","ScheduleFragment onCreateView invoked");
        return v;
    }

    private void setListeners() {
        binding.imageAddScheduleButton.setOnClickListener(v -> {
            ((MainActivity)getActivity()).replaceFragment(new CreateScheduleFragment(), "Create Schedule");
        });
    }

//    private void getSchedules() {
//        @SuppressLint("StaticFieldLeak")
//        class GetScheduleTask extends AsyncTask<Void, Void, List<Schedule>> {
//
//            @Override
//            protected List<Schedule> doInBackground(Void ...voids) {
//                return ScheduleDatabase.getDatabase(getActivity().getApplicationContext()).scheduleDao().getAllSchedules();
//            }
//            @Override
//            protected void onPostExecute(List<Schedule> schedules) {
//                super.onPostExecute(schedules);
//                Log.d("My Schedules", schedules.toString());
//            }
//        }
//        new GetScheduleTask().execute();
//   }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","ScheduleFragment onDestroyView invoked");
    }
}