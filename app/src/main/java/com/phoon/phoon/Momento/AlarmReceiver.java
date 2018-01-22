package com.phoon.phoon.Momento;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Hello on 24/3/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    SharedPreferences preferences;
    private static final int PRIVATE_MODE = 0;
    int colorValue;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = context.getSharedPreferences("notificationSetting", Context.MODE_PRIVATE);


        if (preferences.getString("notification", "").equals("blue")) {
            colorValue = Color.BLUE;
        } else if (preferences.getString("notification", "").equals("yellow")) {
            colorValue = Color.YELLOW;
        } else {
            colorValue = Color.GREEN;
        }

        String Title = intent.getStringExtra(context.getString(R.string.title_msg));
        String Description = intent.getStringExtra("description");
        long id = intent.getExtras().getLong("rowid");
        int reqID = (int) id;
        Log.i("tag", " " + id);

        Intent mainIntent = new Intent (context, MainActivity.class);
        Intent actionIntentOpen = new Intent(context, View_Note.class);
        actionIntentOpen.putExtra("rowID", id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);
        stackBuilder.addNextIntent(actionIntentOpen);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(reqID, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {0, 2000, 500, 500, 500, 500, 500, 500, 500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notify = new Notification.Builder(context)
                .setTicker("Momento")
                .setContentTitle(Title)
                .setContentText(Description)
                .setSmallIcon(R.drawable.notif)
                .setLights(colorValue, 1000 * 3 , 100)
                .setVibrate(pattern)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(notificationPendingIntent).getNotification();
        notify.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        NotificationManager NotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotifyManager.notify(reqID, notify);
    }
}
