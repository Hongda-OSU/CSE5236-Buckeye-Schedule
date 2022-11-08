package com.cse5236.buckeyeschedule.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.adapters.ScheduleAdapter;
import com.cse5236.buckeyeschedule.databinding.FragmentScheduleBinding;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.factory.ScheduleViewModelFactory;
import com.cse5236.buckeyeschedule.listeners.ScheduleListener;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.viewmodel.ScheduleViewModel;

public class ScheduleFragment extends Fragment implements ScheduleListener {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private ScheduleAdapter scheduleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setTitle("My Schedule");
        ((MainActivity) getActivity()).miniIconHelper(true);
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        scheduleViewModel = ViewModelProviders.of(this, new ScheduleViewModelFactory(this.getActivity().getApplication(), ((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_USER_ID))).get(ScheduleViewModel.class);

        //scheduleViewModel.deleteAllSchedule();
        // showToast(Integer.toString(scheduleViewModel.getCountVM()));

        // -> is same as onChange overrides
        scheduleViewModel.getAllSchedules.observe(getViewLifecycleOwner(), schedules -> {
            binding.scheduleRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            );
            scheduleAdapter = new ScheduleAdapter(schedules, this);
            binding.scheduleRecyclerView.setAdapter(scheduleAdapter);
            binding.scheduleRecyclerView.smoothScrollToPosition(0);
        });
        setListeners();
        Log.d("fragment lifecycle", "ScheduleFragment onCreateView invoked");
        return v;
    }

    @Override
    public void onScheduleClicked(Schedule schedule, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("isViewOrUpdate", true);
        bundle.putSerializable("schedule", schedule);
        CreateScheduleFragment createScheduleFragment = new CreateScheduleFragment();
        createScheduleFragment.setArguments(bundle);
        ((MainActivity) getActivity()).replaceFragment(createScheduleFragment, "View Schedule");

    }

    private void setListeners() {
        binding.imageAddScheduleButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).replaceFragment(new CreateScheduleFragment(), "Create Schedule");
        });
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scheduleAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (scheduleViewModel.getCountVM() != 0) {
                    scheduleAdapter.searchSchedule(s.toString());
                }
            }
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
        Log.d("fragment lifecycle", "ScheduleFragment onDestroyView invoked");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}