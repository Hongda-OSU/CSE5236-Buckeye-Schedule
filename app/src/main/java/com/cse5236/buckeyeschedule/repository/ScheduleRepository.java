package com.cse5236.buckeyeschedule.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cse5236.buckeyeschedule.dao.ScheduleDao;
import com.cse5236.buckeyeschedule.database.ScheduleDatabase;
import com.cse5236.buckeyeschedule.entities.Schedule;

import java.util.List;

public class ScheduleRepository {

    public ScheduleDao scheduleDao;
    public LiveData<List<Schedule>> getAllSchedules;

    public ScheduleRepository(Application application) {
        ScheduleDatabase database = ScheduleDatabase.getDatabase(application);
        scheduleDao = database.scheduleDao();
        getAllSchedules = scheduleDao.getAllSchedules();
    }

    public void insertSchedule(Schedule schedule) {
        scheduleDao.insertSchedule(schedule);
    }

    public void deleteSchedule(Schedule schedule) {
        scheduleDao.deleteSchedule(schedule);
    }

    public void updateSchedule(Schedule schedule) {
        scheduleDao.updateSchedule(schedule);
    }

}