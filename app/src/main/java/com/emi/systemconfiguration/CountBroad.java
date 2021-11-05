package com.emi.systemconfiguration;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CountBroad extends BroadcastReceiver {
    String NOTIFICATION_CHANNEL_ID = "ApptheftLocker";
    String channelName = "Notification Service";
    @Override
    public void onReceive(Context context, Intent intent) {

//
//        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Emi-Reminder")
                .setContentText("Your due date is near Please Pay the Amount otherwise the mobile will be made unusable")
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());


    }

}
