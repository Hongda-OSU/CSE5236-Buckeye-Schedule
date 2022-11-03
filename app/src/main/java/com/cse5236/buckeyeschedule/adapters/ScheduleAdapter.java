package com.cse5236.buckeyeschedule.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.fragments.ScheduleFragment;

import java.util.List;


public class ScheduleAdapter extends  RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>{

    private List<Schedule> schedules;

    public ScheduleAdapter(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_schedule,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        holder.setSchedule(schedules.get(position));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.titleText);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutSchedule);
        }

        void setSchedule(Schedule schedule) {
            textTitle.setText(schedule.getScheduleTitle());
            if(schedule.getScheduleSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(schedule.getScheduleSubtitle());
            }
            textDateTime.setText(schedule.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (schedule.getCategory() != null) {
                gradientDrawable.setColor(Color.parseColor(schedule.getCategory()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
        }
    }
}
