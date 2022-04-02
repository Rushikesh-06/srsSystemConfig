package com.emi.systemconfiguration;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserManager;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.os.UserManager.DISALLOW_FACTORY_RESET;

public class BackgroundService extends Service {

    DevicePolicyManager dpm;
    long current_time;
    private Context context ;

    public int counter=0;
    private FirebaseFirestore db;
    ComponentName back;

    public Boolean activeUser = false, userAlert = true, playState = false;

    private BackgroundService backgroundService;
    Intent mServiceIntent;

    MediaPlayer mPlayer ;

    password pass;
    SharedPreferences sharedPreferences;

    private String filename = "q1w2e3r4t5y6u7i8o9p0.txt";

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        back = new ComponentName(this,DeviceAdmin.class);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        pass = password.getInstance();

        mPlayer = MediaPlayer.create(this, R.raw.emisound);

        sharedPreferences = getSharedPreferences("LockingState",MODE_PRIVATE);

//comment it out for hiding the notification
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

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
                .setContentText("This service is under Protection-Mode")
//                .setSmallIcon(R.mipmap.ic_launcher)
             .setPriority(NotificationManager.AUTOMATIC_RULE_STATUS_DISABLED)
//                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
//                .setAutoCancel(true)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startTimer();

        if(isConnected()){
            Log.i("INterent", "========= Connected to  Network ");
            activeDevice();
        }
        else
        {
            Log.i("INterent", "========= Not  Connected to Network ");
            if(activeUser) {
                Intent dialogIntent = new Intent(getApplicationContext(), EmiDueDate.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restart.service");
        broadcastIntent.setClass(this, BackgroundService.class);
        this.sendBroadcast(broadcastIntent);
    }

    private Timer timer;
    private TimerTask timerTask;
    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            // Do something here on the main thread
            try{
                if(readData().equals("true")){
                    dpm.lockNow();
                    Log.d("Lock----->", "LOcked by read write" + readData());
                }
                else {
                    Log.d("Lock----->", "LOcked by read write" + readData());
                }

            }
            catch(Exception e){
                Log.d("Erro", "Ee" +e);
            }


            if(activeUser) {
                if(userAlert){
                    userAlert = false;
                }
                playSound();
                continuesLock();

            }
            handler.postDelayed(runnableCode, 100);
        }
    };

    public void startTimer() {
        int day = 3;
//        handler.post(runnableCode);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void continuesLock(){

        try{
//            ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            String activityName= retriveNewApp(this);
            Log.d("PackageName",activityName);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            String currentLauncherName= resolveInfo.activityInfo.packageName;

            if(activityName.contains("contacts") || activityName.contains("call") || activityName.contains("com.truecaller")){
                Log.e("USer","Vendor is on activity");
//            startActivity(new Intent(this, Lock.class));
//            dialog.show();
            }
            else {
                dpm.lockNow();
            }
            Log.e("Locking", " *********************** THi is woking properly"+ activityName );
        }
        catch (Exception e){
            dpm.lockNow();
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
        try {
            String deviceId = MainActivity.getDeviceId(getApplicationContext());
            Log.d("deviceUid", deviceId);
            DocumentReference documentReference = db.collection("users").document(deviceId);
            DocumentReference drLock = db.collection("users_status").document(deviceId);
            Map<String, Object> status = new HashMap<>();


            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // this method is called when error is not null
                        // and we gt any error
                        // in this cas we are displaying an error message.
                        Log.d("Error is", "Error found" + error);
                        startTimer();
                        return;
                    }
                    if (value != null && value.exists()) {
                        Boolean customerActiveFeild = (Boolean) value.getData().get("customer_active");
                        Log.d("Lock", customerActiveFeild.toString());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("status", customerActiveFeild);
                        editor.apply();

                        if (!customerActiveFeild) {
                            activeUser = customerActiveFeild;
                            Log.d("LockStatus", activeUser.toString());
                            playState= true;
                            status.put("lockStatus",false);
                            drLock.set(status);

                            writeData(customerActiveFeild.toString());


                        } else {
                            activeUser = customerActiveFeild;
                            Log.d("LockStatus2", activeUser.toString());
                            status.put("lockStatus",true);
                            drLock.set(status);
                            writeData(customerActiveFeild.toString());

                            Intent dialogIntent = new Intent(getApplicationContext(), EmiDueDate.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);

                        }
                        Log.d("Found the" + activeUser, value.getData().get("customer_active").toString());
                    }
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
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

    private void playSound(){
        try{
            if(playState.equals(true)){
                mPlayer.start();
//                mPlayer.setLooping(true);
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }

    private void writeData(String status)
    {
        try
        {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            String data = status;
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
            Log.d("---->12","COunt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String readData() {
        try {
            FileInputStream fin = openFileInput(filename);
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


}