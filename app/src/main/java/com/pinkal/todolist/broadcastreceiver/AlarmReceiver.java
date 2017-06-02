package com.pinkal.todolist.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;

import com.pinkal.todolist.R;
import com.pinkal.todolist.activity.AddUpdateActivity;
import com.pinkal.todolist.activity.MainActivity;
import com.pinkal.todolist.activity.ToDoActivity;
import com.pinkal.todolist.utils.Constant;

/**
 * Created by Pinkal Daliya on 18-Oct-16.
 */

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int taskRowId = intent.getExtras().getInt(Constant.TASK_ID);
        String taskTitle = intent.getExtras().getString(Constant.TASK_TITLE);
        String taskTask = intent.getExtras().getString(Constant.TASK_TASK);

        String rowId = String.valueOf(taskRowId);
        Intent notificationIntent = new Intent(context, AddUpdateActivity.class);
        notificationIntent.putExtra(Constant.ROW_ID, rowId);
        notificationIntent.putExtra(Constant.NOTIFICATION, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationFinishIntent = new Intent(context, MainActivity.class);
        notificationFinishIntent.putExtra(Constant.ROW_ID, rowId);
        notificationFinishIntent.putExtra(Constant.NOTIFICATION, true);
        PendingIntent pendingFinishIntent = PendingIntent.getActivity(context, 0, notificationFinishIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = taskTitle;//task title
        String message = taskTask;//task
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setColor(Color.argb(225, 225, 87, 34))
                .setSmallIcon(icon)
                .setWhen(when)
                .addAction(R.drawable.ic_mode_edit_black_24dp, "Edit", pendingIntent)
                .addAction(R.drawable.ic_check_box_black_24dp, "Finish", pendingFinishIntent)
                .setAutoCancel(true)
                .build();
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(Integer.parseInt(rowId), notification);
    }

}
