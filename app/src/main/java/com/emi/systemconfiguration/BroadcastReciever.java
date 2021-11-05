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
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.UserManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;




public class BroadcastReciever extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService locationService;
    Intent mServiceIntent;


    public ComponentName mDeviceAdmin;
    public DevicePolicyManager mDPM;

    Boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(("android.intent.action.BOOT_COMPLETED").equals(action) ||
                ("restart.service").contains(action) ||
                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.ACTION_POWER_CONNECTED").equals(action) ||
                ("android.hardware.usb.action.USB_DEVICE_ATTACHED").equals(action)||
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
        }
    }

    private void startDownload(Context context){
        Toast.makeText(context, "StartetdDownload", Toast.LENGTH_SHORT).show();
        String url = "https://goelectronix.s3.us-east-2.amazonaws.com/AntiTheftV1.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download AntiTheft");
        request.setDescription("Downloading AntiTheft");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"AntiTheftV1.apk");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

}
