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
            import android.net.Uri;
            import android.os.Build;
import android.util.Log;
            import android.view.KeyEvent;
            import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class BroadcastReciever extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService locationService;
    Intent mServiceIntent;

    Boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(("android.intent.action.BOOT_COMPLETED").equals(action) ||
                ("restart service").contains(action) ||
                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.LOCKED_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.BATTERY_CHANGED").contains(action) ||
                ("android.intent.action.ACTION_POWER_CONNECTED").contains((action)) ||
                ("android.intent.action.PACKAGE_REMOVED").contains((action)) ||
                ("BackgroundProcess").equals(action) ||
                ("android.app.action.DEVICE_ADMIN_ENABLED").equals(action)){

//            context.startForegroundService(new Intent(context, BackgroundService.class));
//            context.startForegroundService(new Intent(context, LocationService.class));
            backgroundService = new BackgroundService();
            mServiceIntent = new Intent(context, BackgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mServiceIntent);
            }
            else{
                context.startService(mServiceIntent);
            }

//            locationService = new LocationService();
//            Intent mService= new Intent(context, LocationService.class);
//            context.startService(mService);
//            Log.d("Boot", "Service Started");
//            Intent main = new Intent(context, RegistrationAcitivity.class);
//            context.startActivity(main);

//            Toast.makeText(context,"Service Started",Toast.LENGTH_LONG).show();
//            context.startForegroundService(new Intent(context, BackgroundDelayService.class));

        }







//        Uri data = intent.getData();
//        String mypkg="com.emi.systemconfiguration";
//
//        Log.e("DATA",data+"");
//        Log.e( "Action: " ,intent.getAction());
//
//        if(mypkg.equals(data.toString())){
//            Toast.makeText(context, "Package Installed: ", Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(context, "not match ", Toast.LENGTH_LONG).show();
//        }




//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            backgroundService = new BackgroundService();
//            mServiceIntent = new Intent(context, backgroundService.getClass());
//            context.startService(mServiceIntent);
//            LocationService = new LocationService();
//            mServiceIntent = new Intent(context, LocationService.getClass());
//            context.startService(mServiceIntent);
//            Log.d("Boot", "Service Started");
//            Toast.makeText(context,"2nd Service Started",Toast.LENGTH_LONG).show();
//        } else {
//            backgroundService = new BackgroundService();
//            mServiceIntent = new Intent(context, backgroundService.getClass());
//            context.startService(mServiceIntent);
//            LocationService = new LocationService();
//            mServiceIntent = new Intent(context, LocationService.getClass());
//            context.startService(mServiceIntent);
//            Log.d("Boot", "Service Started");
//            Toast.makeText(context,"3rd Service Started",Toast.LENGTH_LONG).show();
//        }



//        /*-------alarm setting after boot again--------*/
//        Intent alarmIntent = new Intent(context, BackgroundService.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 800, alarmIntent, 0);
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int interval = (86400 * 1000) / 2;
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);


    }

}
