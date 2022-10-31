package com.cse5236.buckeyeschedule.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Schedule_Database")
public class Schedule {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "schedule_id")
    public int scheduleId;

    @ColumnInfo(name = "schedule_title")
    public String scheduleTitle;

    @ColumnInfo(name = "schedule_description")
    public String scheduleDescription;

    @ColumnInfo(name = "due_date")
    public String dueDate;

    @ColumnInfo(name = "user_id")
    public String userId;

//    @ColumnInfo(name = "recording_path")
//    public String recordingPath;

//    @ColumnInfo(name = "status")
//    public String status;

//    @ColumnInfo(name = "add_reminder")
//    public boolean addReminder;

//    @ColumnInfo(name = "remind_days")
//    public int remindDays;

//    @ColumnInfo(name = "location")
//    public String location;

    @ColumnInfo(name = "category")
    public String category;


}
