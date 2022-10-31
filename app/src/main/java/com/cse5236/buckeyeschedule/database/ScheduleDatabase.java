package com.cse5236.buckeyeschedule.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cse5236.buckeyeschedule.dao.ScheduleDao;
import com.cse5236.buckeyeschedule.entities.Schedule;

@Database(entities = {Schedule.class}, version = 1, exportSchema = false)
public abstract class ScheduleDatabase extends RoomDatabase {

    public abstract ScheduleDao scheduleDao();

    public static ScheduleDatabase scheduleDatabase;

    public static synchronized ScheduleDatabase getDatabase(Context context) {

        if (scheduleDatabase == null) {
            scheduleDatabase = Room.databaseBuilder(context,
                    ScheduleDatabase.class,
                    "Schedule_Database").build();
        }
        return scheduleDatabase;
    }

}