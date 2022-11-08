package com.cse5236.buckeyeschedule.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.listeners.ScheduleListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> schedules;
    private List<Schedule> scheduleSource;
    private ScheduleListener scheduleListener;
    private Timer timer;

    public ScheduleAdapter(List<Schedule> schedules, ScheduleListener scheduleListener) {
        this.schedules = schedules;
        scheduleSource = schedules;
        this.scheduleListener = scheduleListener;
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
        holder.layoutSchedule.setOnClickListener(v -> {
            scheduleListener.onScheduleClicked(schedules.get(position), position);
        });
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
        LinearLayout layoutSchedule;
        RoundedImageView imageSchedule;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.titleText);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutSchedule = itemView.findViewById(R.id.layoutSchedule);
            imageSchedule = itemView.findViewById(R.id.imageSchedule);
        }

        void setSchedule(Schedule schedule) {
            textTitle.setText(schedule.getScheduleTitle());
            if (schedule.getScheduleSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(schedule.getScheduleSubtitle());
            }
            textDateTime.setText(schedule.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutSchedule.getBackground();
            if (schedule.getCategory() != null) {
                gradientDrawable.setColor(Color.parseColor(schedule.getCategory()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (schedule.getImagePath() != null) {
                imageSchedule.setImageBitmap(BitmapFactory.decodeFile(schedule.getImagePath()));
                imageSchedule.setVisibility(View.VISIBLE);
            } else {
                imageSchedule.setVisibility(View.GONE);
            }
        }
    }

    public void searchSchedule(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    schedules = scheduleSource;
                } else {
                    ArrayList<Schedule> s = new ArrayList<>();
                    for (Schedule schedule : scheduleSource) {
                        if (schedule.getScheduleTitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                schedule.getScheduleSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                schedule.getScheduleDescription().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            s.add(schedule);
                        }
                    }
                    schedules = s;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
