package com.emi.systemconfiguration;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class BackgroundDelayService extends Service {
    public int counter=0;
    Dialog dialog;
    private FirebaseFirestore db;

    public String activeUser = "true";

    private BackgroundDelayService BackgroundDelayService;
    Intent mServiceIntent;

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence2";
        String channelName = "Notification Service";
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

//        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 100,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.system_icon)
                .setContentTitle("Emi-Reminder")
                .setContentText("Your due date is near Please Pay the Amount otherwise the mobile will be made unusable")
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());
//        startForeground(100, builder.build());
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
        broadcastIntent.setAction("restart Counter service");
        broadcastIntent.setClass(this, BackgroundDelayService.class);
        this.sendBroadcast(broadcastIntent);
    }



    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
//        int day = 2073600 ;
        int day = 30;
        timer = new Timer();
        timerTask = new TimerTask() {

            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void run() {
                Log.i("Counter reminder", "=========  "+ (counter++));
               // checkRunningApps();
                if(day <= counter){
                    Log.i("Counter of reminder", "========= Workingggg  ");
                    //    activeDevice();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                        startMyOwnForeground();
                    else
                        startForeground(3, new Notification());
                    counter =0;
                }

            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }



    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            BackgroundDelayService = new BackgroundDelayService();
            mServiceIntent = new Intent(getApplicationContext(), BackgroundDelayService.getClass());
            stopService(mServiceIntent);
        }

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
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
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

                    if(!customerActiveFeild){
                        stoptimertask();
                        Log.i("Count", "========= Stopped");
                    }
                    else {
                        BackgroundDelayService = new BackgroundDelayService();
                        mServiceIntent = new Intent(getApplicationContext(), BackgroundDelayService.getClass());
                        if (!isMyServiceRunning(BackgroundDelayService.getClass())) {
                            startForegroundService(mServiceIntent);
                        }

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
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}