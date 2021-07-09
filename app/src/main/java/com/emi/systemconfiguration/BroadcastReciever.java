package com.emi.systemconfiguration;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class BroadcastReciever extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";

    Boolean screenOff;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
//            Intent i = new Intent(context, MainActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
//        }
        String action = intent.getAction();
        if("android.intent.action.BOOT_COMPLETED".equals(action)){
            Intent BGservice = new Intent(context, BackgroundService.class);
            context.startService(BGservice);
            Intent LocationService = new Intent(context, LocationService.class);
            context.startService(LocationService);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, BackgroundService.class));
//            context.startForegroundService(new Intent(context, BackgroundDelayService.class));
            context.startForegroundService(new Intent(context, LocationService.class));
        } else {
            context.startForegroundService(new Intent(context, BackgroundService.class));
//            context.startForegroundService(new Intent(context, BackgroundDelayService.class));
            context.startForegroundService(new Intent(context, LocationService.class));
        }



//        /*-------alarm setting after boot again--------*/
//        Intent alarmIntent = new Intent(context, BackgroundService.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 800, alarmIntent, 0);
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int interval = (86400 * 1000) / 2;
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }
}
