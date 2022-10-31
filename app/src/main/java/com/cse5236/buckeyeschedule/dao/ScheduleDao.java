package com.cse5236.buckeyeschedule.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cse5236.buckeyeschedule.entities.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM Schedule_Database ORDER BY schedule_id DESC")
    LiveData<List<Schedule>> getAllSchedules();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSchedule(Schedule schedule);

    @Query("SELECT * FROM Schedule_Database WHERE schedule_id= :scheduleId")
    Schedule getSingleSchedule(int scheduleId);

    @Update
    void updateSchedule(Schedule schedule);

    @Delete
    void deleteSchedule(Schedule schedule);
}