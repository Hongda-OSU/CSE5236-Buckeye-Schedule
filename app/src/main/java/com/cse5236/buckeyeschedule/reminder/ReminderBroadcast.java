package com.cse5236.buckeyeschedule.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cse5236.buckeyeschedule.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyBuckeyeSchedule")
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                .setContentTitle("Reminder from Buckeye Schedule")
                .setContentText("Hey, you have a schedule to complete!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }
}
