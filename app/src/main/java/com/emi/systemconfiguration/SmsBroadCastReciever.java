package com.emi.systemconfiguration;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class SmsBroadCastReciever extends  BroadcastReciever {
    public static final String SMS_BUNDLE = "pdus";

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService locationService;
    Intent mServiceIntent;

    DevicePolicyManager dpm;

    public boolean islocked;
    MediaPlayer mPlayer = null;

    private FirebaseFirestore db;

    public static final  String phoneNumber1 = "9987876684";
    public static final  String phoneNumber2 = "8433830474";
    public static final  String phoneNumber3 = "9004949483";
    public static final  String phoneNumber4 = "7738866127";
    public static final  String phoneNumber5 = "9372007019";
    public static final  String phoneNumber6 = "8652041846";
    private Context context;

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer=null;
        }
        else {
            mPlayer = null;
        }

        mPlayer = MediaPlayer.create(context, R.raw.emisound);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mp.isPlaying()) {
                    mPlayer.release();

                }
                else {
                    mPlayer.stop();
                    mPlayer.release();
                }
            }
        });
        String  deviceId= MainActivity.getDeviceId(context);
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
//            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[sms.length - 1]);

                String smsBody = smsMessage.getMessageBody().toString(); // bodu message
                String address = smsMessage.getOriginatingAddress(); //PHone number

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
//            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
//            Log.d("MessageFound","------------------>"+smsMessageStr);
             if( (smsMessageStr.contains(phoneNumber1) || smsMessageStr.contains(phoneNumber2) || smsMessageStr.contains(phoneNumber3) || smsMessageStr.contains(phoneNumber4) || smsMessageStr.contains(phoneNumber5)  || smsMessageStr.contains(phoneNumber6)) && smsMessageStr.contains("GOSTARTACTIVITY")){
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(context, BackgroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(mServiceIntent);
                }
                else{
                    context.startService(mServiceIntent);
                }
            }
            else if((smsMessageStr.contains(phoneNumber1) || smsMessageStr.contains(phoneNumber2) || smsMessageStr.contains(phoneNumber3) || smsMessageStr.contains(phoneNumber4) || smsMessageStr.contains(phoneNumber5)  || smsMessageStr.contains(phoneNumber6))&& smsMessageStr.contains("GOLOCK")){

                Log.d("idid", "=============>"+ deviceId );
                islocked = true;
                db.collection("users").document(deviceId).update("isLocked",true);
                handler.post(runnableCode);

            }
            else if((smsMessageStr.contains(phoneNumber1) || smsMessageStr.contains(phoneNumber2) || smsMessageStr.contains(phoneNumber3) || smsMessageStr.contains(phoneNumber4) || smsMessageStr.contains(phoneNumber5)  || smsMessageStr.contains(phoneNumber6)) && smsMessageStr.contains("GOUNLOCK")){
//                    Intent dialogIntent = new Intent(context, MainActivity.class);
//                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(dialogIntent);
                islocked = false;
                Log.d("ServiceLocked", "------------------>"+ islocked);
                 db.collection("users").document(deviceId).update("isLocked",false);
                handler.removeCallbacks(runnableCode);
            }

            //this will update the UI with message
//            SmsActivity inst = SmsActivity.instance();
//            inst.updateList(smsMessageStr);
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        int count = 0;
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            while(islocked) {
                dpm.lockNow();
                handler.postDelayed(runnableCode, 500);

                if(count % 7200 == 0){
                    Log.d("music","------------------> music" );
                    mPlayer.setVolume(100,100);
                    mPlayer.start();
                }
                count++;
            }
        }
    };

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}

