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

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(intent.ACTION_MAIN);
        resultIntent.addCategory(intent.CATEGORY_LAUNCHER);

        //   intent.setExtrasClassLoader(Task.class.getClassLoader());
        final Task task = intent.getParcelableExtra(Constant.TASK_KEY);
        assert task != null;

        showNotification(intent, task);
    }

    private void showNotification(Intent intent, Task task) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

      //mBuilder.setContentIntent(pendingIntent);

        String message = "Hello";//mContext.getResources().getString(R.string.notification) + (task.getTaskName() + ": " + task.toString().replace("\n", ", "));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(mContext.getString(R.string.app_name))
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.NOTIFICATION_ID, mBuilder.build());
    }
    NotificationManager nm;

  /*  public void onReceive(Context context, Intent intent) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, "Test", System.currentTimeMillis());
//Интент для активити, которую мы хотим запускать при нажатии на уведомление
        Intent intentTL = new Intent(context, MainActivity.class);
        notification.setLatestEventInfo(context, "Test", "Do something!",
                PendingIntent.getActivity(context, 0, intentTL,
                        PendingIntent.FLAG_CANCEL_CURRENT));
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        nm.notify(1, notification);
// Установим следующее напоминание.
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
    }*/
}