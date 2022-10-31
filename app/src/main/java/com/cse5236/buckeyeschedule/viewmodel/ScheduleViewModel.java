package com.cse5236.buckeyeschedule.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.repository.ScheduleRepository;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

    public ScheduleRepository repository;
    public LiveData<List<Schedule>> getAllSchedules;

    public ScheduleViewModel(Application application) {
        super(application);

        repository = new ScheduleRepository(application);
        getAllSchedules = repository.getAllSchedules;
    }

    void insertSchedule(Schedule schedule) {
        repository.insertSchedule(schedule);
    }

    void updateSchedule(Schedule schedule) {
        repository.updateSchedule(schedule);
    }

    void deleteSchedule(Schedule schedule) {
        repository.deleteSchedule(schedule);
    }
}