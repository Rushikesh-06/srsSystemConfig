package com.emi.systemconfiguration;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class PowerButtonService extends Service {

    DevicePolicyManager dpm;
    long current_time;
    Timer myThread;
    private Context context ;

    public int counter=0;
    Dialog dialog;
    private FirebaseFirestore db;

    public Boolean activeUser = false, userAlert = true;

    private PowerButtonService powerButtonService;
    Intent mServiceIntent;

    MediaPlayer mPlayer ;

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();
//comment it out for hiding the notification
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());



        mPlayer = MediaPlayer.create(this, R.raw.emisound);
        mPlayer.setLooping(false);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(Color.BLUE);
        }
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setPriority(Notification.PRIORITY_MIN);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.system_icon)
                .setContentTitle( "System Service")
                .setContentText("Welcome Mode")
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationManager.AUTOMATIC_RULE_STATUS_DISABLED)
//                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
//                .setAutoCancel(true)
                .build();
//                .setContentTitle("System Service")
//                .setContentText("This service is under Protection-Mode")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("Ticker text")
//                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
//                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restart service");
        broadcastIntent.setClass(this, PowerButtonService.class);
        this.sendBroadcast(broadcastIntent);
    }


    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        int day = 3;
        int count = 0;
        String myPackage;
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            checkRunningApps();

            handler.postDelayed(runnableCode, 500);
        }
    };


    public void startTimer() {
        handler.post(runnableCode);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void continuesLock(){
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        try{
//            ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            String activityName= retriveNewApp(this);
            Log.d("PackageName",activityName);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String currentLauncherName= resolveInfo.activityInfo.packageName;
//|| activityName.equals(currentLauncherName)
            if(activityName.contains("contacts") || activityName.contains("call") || activityName.contains("com.truecaller") || activityName.equals("com.emi.systemconfiguration")){
                Log.e("USer","Vendor is on activity");
//            startActivity(new Intent(this, Lock.class));
//            dialog.show();

            }
            else {

                dpm.lockNow();

//                if(count % 10 == 0){
//                    Log.d("music","------------------> music"+ count );
//                    mPlayer.setVolume(100,100);
//                    mPlayer.start();
//                }

            }
            Log.e("Locking", " *********************** THi is woking properly"+ activityName );
        }
        catch (Exception e){
//            Toast.makeText(this, "Please Contact Vendor",Toast.LENGTH_LONG).show();
//            Alert();
//            dpm.lockNow();
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void checkRunningApps() {

        String myPackage;
        myPackage = retriveNewApp(this);
        Log.e("app","app details are" + myPackage);
//        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
//        "com.android.settings"
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String currentLauncherName= resolveInfo.activityInfo.packageName;
        Log.d("Launcher", currentLauncherName);
        if(myPackage.contains("com.emi.systemconfiguration")){
            Log.e("Tag", "-------------* THi is woking properly");
        }
        else{
//            Log.e("Tag", "-------------* THi is not woking properly");
            showWelcomeActivity();
        }
    }

    public void showWelcomeActivity(){
        Intent dialogIntent = new Intent(this, Welcome.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(am != null) {
            List<ActivityManager.AppTask> tasks = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                tasks = am.getAppTasks();
            }
            if (tasks != null && tasks.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tasks.get(0).setExcludeFromRecents(true);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void checkHomelauncher(){
        String myPackage;
        myPackage = retriveNewApp(this);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> lst = getPackageManager().queryIntentActivities(intent, 0);

        Log.d("HomeLaunche", lst.toString());

//        if (!lst.isEmpty()) {
//            for (ResolveInfo resolveInfo : lst) {
//                Log.d("Test", "New Launcher Found: " + resolveInfo.activityInfo.packageName +"Foreground package"+ myPackage);
//                if(resolveInfo.activityInfo.packageName.equals(myPackage) ||  myPackage.contains(("com.emi.systemconfiguration"))  || myPackage.contains(("com.whatsapp")) || myPackage.contains("com.google.android.youtube")){
//                    Intent dialogIntent = new Intent(this, Lock.class);
//                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(dialogIntent);
//                }
//            }
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static final String retriveNewApp(Context context) {
//        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
        if (Build.VERSION.SDK_INT >= 21) {
            String currentApp = null;
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 500 * 500, time);
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

    private void activeDevice(){
        String  deviceId=MainActivity.getDeviceId(getApplicationContext());
//        RegistrationAcitivity register = new RegistrationAcitivity();
        //   String status = register.activeUser(context);
        //   Log.d("gdfhhjgdfhdf",status);
        // Log.d("DeviceID",deviceId.toString());
        DocumentReference documentReference = db.collection("users").document(deviceId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // this method is called when error is not null
                    // and we gt any error
                    // in this cas we are displaying an error message.
                    Log.d("Error is","Error found" + error);
                    startTimer();
                    return;
                }
                if (value != null && value.exists()) {
                    Boolean customerActiveFeild = (Boolean) value.getData().get("customer_active");
//                    Log.d("Lock",customerActiveFeild.toString());
                    if(!customerActiveFeild){
//                        stoptimertask();
//                        Log.i("Count", "========= Stopped");
                        activeUser = customerActiveFeild;
                        documentReference.update("isLocked",false);
                    }
                    else {
                        activeUser = customerActiveFeild;
                        documentReference.update("isLocked",true);
                    }
                    Log.d("Found the"+activeUser, value.getData().get("customer_active").toString());
                }
            }
        });
//        return deviceId;
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}