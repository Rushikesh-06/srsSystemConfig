package com.emi.systemconfiguration;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


public class BroadcastReciever extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private UninstallService uninstallService;
    Intent mServiceIntent;
    Intent getmServiceIntent;


    public ComponentName mDeviceAdmin;
    DevicePolicyManager dpm;

    Boolean screenOff;
    SharedPreferences sharedPreferences ;
    private final String filename = "q1w2e3r4t5y6u7i8o9p0.txt";

    Context context1;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        sharedPreferences = context. getSharedPreferences("LockingState",Context.MODE_PRIVATE);
        Boolean status = sharedPreferences.getBoolean("status", false);
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        context1 = context;

        if(("android.intent.action.BOOT_COMPLETED").equals(action) ||
                ("restart.service").contains(action) ||
                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.ACTION_LOCKED_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.ACTION_POWER_CONNECTED").equals(action) ||
                ("android.hardware.usb.action.USB_DEVICE_ATTACHED").equals(action)||
                ("android.intent.action.LOCKED_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.BATTERY_CHANGED").contains(action) ||
                ("android.intent.action.ACTION_POWER_CONNECTED").contains((action)) ||
                ("android.intent.action.PACKAGE_REMOVED").contains((action)) ||
                ("android.intent.action.ACTION_SHUTDOWN").contains((action)) ||
                ("android.intent.action.AIRPLANE_MODE").contains((action)) ||
                ("android.intent.action.SCREEN_ON").contains((action)) ||
                ("android.intent.action.SCREEN_OFF").contains((action)) ||
                ("android.intent.action.CONFIGURATION_CHANGED").contains((action)) ||
                ("android.intent.action.REBOOT").contains((action)) ||
                ("BackgroundProcess").equals(action) ||
                ("android.app.action.DEVICE_ADMIN_ENABLED").equals(action)){

            Log.d("---------->d", Objects.requireNonNull(
                    readData(context)));

            startService();
            try{
                try{
                    if(Objects.requireNonNull(readData(context)).equals("true") ){
                        Log.d("---------->d1", readData(context));
                        backgroundService = new BackgroundService();
                        mServiceIntent = new Intent(context, BackgroundService.class);
                            context.startService(mServiceIntent);

                    }
                }
                catch(Exception e){
                    Log.d("Ex", "Ex"+ e);

                }
                if(status) {
//                    dpm.lockNow();
                    backgroundService = new BackgroundService();
                    mServiceIntent = new Intent(context, BackgroundService.class);
                        context.startService(mServiceIntent);

                }
            }
            catch(Exception e){
                Log.d("Err","Error"+ e);
            }

        }
    }

    private String readData(Context context) {
        try {
            FileInputStream fin = context.openFileInput(filename);
            int a;
            StringBuilder temp = new StringBuilder();
            while ((a = fin.read()) != -1) {
                temp.append((char) a);
            }

            // setting text from the file.
            String data = temp.toString();
            fin.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void startService(){
        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(context1, BackgroundService.class);

            context1.startService(mServiceIntent);

        uninstallService = new UninstallService();
        getmServiceIntent = new Intent(context1, uninstallService.getClass());


            context1.startService(getmServiceIntent);

    }


}
