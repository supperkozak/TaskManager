package com.example.taskmanager.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.taskmanager.R;
import com.example.taskmanager.activity.MainActivity;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.model.Task;

public class TaskNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       // Intent resultIntent = new Intent(context, MainActivity.class);
        Task task = intent.getParcelableExtra(Constant.TASK_KEY);


        showNotification(task, context);
    }

    private void showNotification(Task task, Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            String message = context.getResources().getString(R.string.task_comment) + " " + task.getTaskName() + " " +
                    context.getResources().getString(R.string.notification);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(context.getResources().getString(R.string.app_name))
                            .setTicker(context.getResources().getString(R.string.ticker))
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setContentIntent(notificationIntent)
                            .setDefaults(Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constant.NOTIFICATION_ID, mBuilder.build());
    }
}