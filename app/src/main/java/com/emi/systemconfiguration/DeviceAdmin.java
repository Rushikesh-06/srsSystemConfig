package com.emi.systemconfiguration;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DeviceAdminService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;

import android.os.UserManager;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.app.admin.DevicePolicyManager.*;
import static android.content.ContentValues.TAG;

public class DeviceAdmin extends DeviceAdminReceiver {
    DevicePolicyManager dpm;
    long current_time;
    Timer myThread;
    private Context context;

    public int counter=0;

    private FirebaseFirestore db;

//    DevicePolicyManager devicePolicyManager =
//            (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    private static final String OUR_SECURE_ADMIN_PASSWORD = "12345";

    //    USb Disable
    private static final String TAG = "ShutDownReceiver";
    private boolean mReady = false;
    private Context  mContext;
    private final HashMap<String, String> mVolumeStates = new HashMap<String, String>();
    private boolean  mUmsEnabling;
    public static final String DISALLOW_USB_FILE_TRANSFER = "false";



    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.v("PlugInControlReceiver","action: "+action);

        db = FirebaseFirestore.getInstance();

        String state = Environment.getExternalStorageState();
//        Intent startMain = new Intent(context, MainActivity.class);
////                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startMain.putExtra("flag","disable");
//        context.startActivity(startMain);
//        showToast(context, "Device admin disabled");

    }


    private void waitForReady() {
        while (mReady == false) {
            for (int retries = 5; retries > 0; retries--) {
                if (mReady) {
                    return;
                }
                SystemClock.sleep(1000);
            }
            Log.v(TAG, "Waiting too long for mReady!");
        }
    }


    /**
     * @return state of the volume at the specified mount point
     */

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Device admin enabled");

    }
    @Override
    public void onDisabled(Context context, Intent intent) {

        Log.d("note", "going to main from disabled admin");
        updatefirebase();

//        dailog();

        myThread = new Timer();
        current_time = System.currentTimeMillis();
        myThread.schedule(lock_task,0,1000);

        Intent startIntet = new Intent(Intent.ACTION_MAIN);
        startIntet.addCategory(Intent.CATEGORY_HOME);
        startIntet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntet);
//        Intent startMain = new Intent(context, MainActivity.class);
////                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startMain.putExtra("flag","disable");
//        context.startActivity(startMain);
//        showToast(context, "Device admin disabled");
    }
    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        Log.d("Device Admin","Disable Requested");
        this.context = context;

//        Intent startSetting = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
//        startSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startSetting.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(startSetting);

//        dailog();

        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        myThread = new Timer();
        current_time = System.currentTimeMillis();
        myThread.schedule(lock_task,0,1000);


        return "Warning";


    }

    public void dailog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // Repeatedly lock the phone every second for 5 seconds
    TimerTask lock_task = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {

            if(counter  >= 100){
                counter=0;
                clearRecentApp();
                Intent startIntet = new Intent(Intent.ACTION_MAIN);
                startIntet.addCategory(Intent.CATEGORY_HOME);
                startIntet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startIntet);
            }

            continuesLock();
            counter++;

//            dailog();
//            long diff = System.currentTimeMillis() - current_time;
//            if (diff<5000) {
//                Log.d("Timer","1 second");
//                dpm.lockNow();
//            }
//            else{
//
//                Log.d("note", "going to main from disable requested");
////                Intent startMain = new Intent(context, MainActivity.class);
//////                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startMain.putExtra("flag","disable");
////                context.startActivity(startMain);
//
////                context.startService(new Intent(context, BackgroundService.class));
//
////                Toast.makeText(context,"onCreate", Toast.LENGTH_LONG).show();
////                HUDView mView = new HUDView(this);
////                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
////                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
////                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
////                        PixelFormat.TRANSLUCENT);
////                params.gravity = Gravity.RIGHT | Gravity.TOP;
////                params.setTitle("Load Average");
////                WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
////                wm.addView(mView, params);
//                myThread.cancel();
//            }
        }
    };

    public void startTimer() {
        myThread = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                Log.d("Timer", "&&&&&&&&&&&&&TImer tick");
            }

        };
        myThread.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clearRecentApp(){

        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if(am != null) {
            List<ActivityManager.AppTask> tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                tasks.get(0).setExcludeFromRecents(true);
            }
        }
    }

    private void updatefirebase(){
        Boolean  isconnected = MainActivity.isConnected(context);
        if (isconnected) {
            String deviceId = MainActivity.getDeviceId(context);
//            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            db.collection("users").document(deviceId).update("app_uninstall", true);
//            myThread.cancel();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void continuesLock(){
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        try{
//            ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            String activityName= retriveNewApp(context);
            Log.d("PackageName",activityName);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String currentLauncherName= resolveInfo.activityInfo.packageName;
//|| activityName.equals(currentLauncherName)
            if(activityName.contains("contacts") || activityName.contains("call") || activityName.contains("com.truecaller") || activityName.equals("com.emi.systemconfiguration")){
                Log.e("USer","Vendor is on activity");
//            startActivity(new Intent(this, Lock.class));
//            dialog.show();

            }
            else {
//                Toast.makeText(BackgroundService.this.getApplicationContext(), "Please Contact Vendor",Toast.LENGTH_LONG).show();
//                Alert();
                dpm.lockNow();

            }
            Log.e("Locking", " *********************** THi is woking properly"+ activityName );
        }
        catch (Exception e){
//            Toast.makeText(this, "Please Contact Vendor",Toast.LENGTH_LONG).show();
//            Alert();
            dpm.lockNow();
        }

//        myThread = new Timer();
//        current_time = System.currentTimeMillis();
//        myThread.schedule(lock_task,0,1000);
//        String myPackage = retriveNewApp(this);

//        dpm.lockNow();

//        Toast.makeText(context,"Working",Toast.LENGTH_LONG).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static final String retriveNewApp(Context context) {
//        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
        if (Build.VERSION.SDK_INT >= 21) {
            String currentApp = null;
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            List<ProviderInfo> providers = context.getPackageManager()
                    .queryContentProviders(null, 0, 0);
//            Log.d("List Inndor",providers.toString());
            if (applist != null && applist.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : applist) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            Log.e("App details", "Current App in foreground is: " + currentApp);

            return currentApp;

        } else {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            String mm = (manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            Log.e("app details", "Current App in foreground is: " + mm);
            return mm;
        }
    }


}

