package com.cse5236.buckeyeschedule.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Schedule_Database")
public class Schedule implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "schedule_id")
    public int scheduleId;

    @ColumnInfo(name = "schedule_title")
    public String scheduleTitle;

    @ColumnInfo(name = "schedule_subTitle")
    public String scheduleSubtitle;

    @ColumnInfo(name = "date_time")
    public String dateTime;

    @ColumnInfo(name = "schedule_description")
    public String scheduleDescription;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "image_path")
    public String imagePath;

    @ColumnInfo(name = "web_link")
    public String webLink;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public String getScheduleSubtitle() {
        return scheduleSubtitle;
    }

    public void setScheduleSubtitle(String scheduleSubtitle) {
        this.scheduleSubtitle = scheduleSubtitle;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }

    public void setScheduleDescription(String scheduleDescription) {
        this.scheduleDescription = scheduleDescription;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @NonNull
    @Override
    public String toString() {
        return scheduleTitle + " : " + dateTime;
    }


//    @ColumnInfo(name = "due_date")
//    public String dueDate;

//    @ColumnInfo(name = "recording_path")
//    public String recordingPath;

//    @ColumnInfo(name = "notification")
//    public string notification;

//    @ColumnInfo(name = "location")
//    public String location;

}
