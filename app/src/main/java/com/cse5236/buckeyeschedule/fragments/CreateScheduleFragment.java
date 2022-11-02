package com.cse5236.buckeyeschedule.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentCreateScheduleBinding;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.viewmodel.ScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateScheduleFragment extends Fragment {

    private FragmentCreateScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setTitle("Create Schedule");
        ((MainActivity) getActivity()).miniIconHelper(false);
        binding = FragmentCreateScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        setTime();
        setListeners();
        Log.d("fragment lifecycle", "CreateScheduleFragment onCreateView invoked");
        return v;
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).onBackPressed();
        });
        binding.imageSave.setOnClickListener(v -> saveSchedule());
    }

    private void setTime() {
        binding.textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
    }

    private void saveSchedule() {
        if (binding.inputScheduleTitle.getText().toString().trim().isEmpty()) {
            showToast("Schedule title can't be empty");
            return;
        } else if (binding.inputScheduleSubtitle.getText().toString().trim().isEmpty()) {
            showToast("Schedule subtitle can't be empty");
            return;
        } else if (binding.inputSchedule.getText().toString().trim().isEmpty()) {
            showToast("Schedule description can't be empty");
            return;
        }

        final Schedule schedule = new Schedule();
        schedule.setScheduleTitle(binding.inputScheduleTitle.getText().toString());
        schedule.setScheduleSubtitle(binding.inputScheduleSubtitle.getText().toString());
        schedule.setScheduleDescription(binding.inputSchedule.getText().toString());
        schedule.setDateTime(binding.textDateTime.getText().toString());
        schedule.setUserId(((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_USER_ID));

        scheduleViewModel.insertSchedule(schedule);

        ((MainActivity) getActivity()).replaceFragment(new ScheduleFragment(), "Schedule");

//        class SaveScheduleTask extends AsyncTask<Void, Void, Void> {
//            @Override
//            protected Void doInBackground(Void ... voids) {
//                ScheduleDatabase.getDatabase(getActivity().getApplicationContext()).scheduleDao().insertSchedule(schedule);
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                ((MainActivity) getActivity()).replaceFragment(new ScheduleFragment(), "Schedule");
//            }
//        }
//        new SaveScheduleTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle", "CreateScheduleFragment onDestroyView invoked");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}