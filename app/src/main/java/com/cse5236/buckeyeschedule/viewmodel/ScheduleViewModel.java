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

    public void insertSchedule(Schedule schedule) {
        repository.insertSchedule(schedule);
    }

    public void updateSchedule(Schedule schedule) {
        repository.updateSchedule(schedule);
    }

    public void deleteSchedule(int id) {
        repository.deleteSchedule(id);
    }

    public void deleteAllSchedule() {repository.deleteAllSchedule();}
}