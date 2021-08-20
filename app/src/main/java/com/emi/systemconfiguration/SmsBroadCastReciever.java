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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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

    public static final  String phoneNumber = "7977433970";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
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
            Log.d("MessageFound","------------------>"+smsMessageStr);

            if( smsMessageStr.contains(phoneNumber) && smsMessageStr.contains("Gostartactivity")){
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(context, BackgroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(mServiceIntent);
                }
                else{
                    context.startService(mServiceIntent);
                }
            }
            else if(smsMessageStr.contains(phoneNumber) && smsMessageStr.contains("Golock")){
                islocked = true;
                handler.post(runnableCode);

            }
            else if(smsMessageStr.contains(phoneNumber) && smsMessageStr.contains("Gounlock")){
//                    Intent dialogIntent = new Intent(context, MainActivity.class);
//                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(dialogIntent);
                islocked = false;
                Log.d("ServiceLocked", "------------------>"+ islocked);
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


}