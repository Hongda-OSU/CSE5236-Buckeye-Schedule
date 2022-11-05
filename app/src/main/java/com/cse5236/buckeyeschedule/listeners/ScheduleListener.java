package com.cse5236.buckeyeschedule.listeners;

import com.cse5236.buckeyeschedule.entities.Schedule;

public interface ScheduleListener {
    void onScheduleClicked(Schedule schedule, int position);
}
