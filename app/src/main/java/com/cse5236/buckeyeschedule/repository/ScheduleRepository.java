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

    public ScheduleRepository(Application application, final String userId) {
        ScheduleDatabase database = ScheduleDatabase.getDatabase(application);
        scheduleDao = database.scheduleDao();
        getAllSchedules = scheduleDao.getAllSchedules(userId);
    }

    public void insertSchedule(Schedule schedule) {
        scheduleDao.insertSchedule(schedule);
    }

    public void deleteSchedule(int id) {
        scheduleDao.deleteSchedule(id);
    }

    public void updateSchedule(Schedule schedule) {
        scheduleDao.updateSchedule(schedule);
    }

    public void deleteAllSchedule() {
        scheduleDao.deleteAllSchedule();
    }

    public LiveData<Integer> getCount() {
        return scheduleDao.getCount();
    }

    public int getCountDAO() {
        return scheduleDao.getRowCount();
    }
}