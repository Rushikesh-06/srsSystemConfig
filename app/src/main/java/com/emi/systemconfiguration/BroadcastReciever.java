package com.emi.systemconfiguration;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class BroadcastReciever extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService LocationService;
    Intent mServiceIntent;

    Boolean screenOff;

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {

//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
//            Intent i = new Intent(context, MainActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
//        }
        String action = intent.getAction();
        if(("android.intent.action.BOOT_COMPLETED").equals(action) || ("restart service").contains(action) || ("android.intent.action.QUICKBOOT_POWERON").equals(action)){

//            context.startForegroundService(new Intent(context, BackgroundService.class));
//            context.startForegroundService(new Intent(context, LocationService.class));
            backgroundService = new BackgroundService();
            mServiceIntent = new Intent(context, backgroundService.getClass());
            context.startService(mServiceIntent);
            LocationService = new LocationService();
            mServiceIntent = new Intent(context, LocationService.getClass());
            context.startService(mServiceIntent);
            Log.d("Boot", "Service Started");
//            context.startForegroundService(new Intent(context, BackgroundDelayService.class));

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            backgroundService = new BackgroundService();
            mServiceIntent = new Intent(context, backgroundService.getClass());
            context.startService(mServiceIntent);
            LocationService = new LocationService();
            mServiceIntent = new Intent(context, LocationService.getClass());
            context.startService(mServiceIntent);
            Log.d("Boot", "Service Started");
        } else {
            backgroundService = new BackgroundService();
            mServiceIntent = new Intent(context, backgroundService.getClass());
            context.startService(mServiceIntent);
            LocationService = new LocationService();
            mServiceIntent = new Intent(context, LocationService.getClass());
            context.startService(mServiceIntent);
            Log.d("Boot", "Service Started");
        }



//        /*-------alarm setting after boot again--------*/
//        Intent alarmIntent = new Intent(context, BackgroundService.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 800, alarmIntent, 0);
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int interval = (86400 * 1000) / 2;
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }



}
