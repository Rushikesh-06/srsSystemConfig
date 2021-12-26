package com.emi.systemconfiguration;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.annotation.Nullable;

import static android.service.controls.ControlsProviderService.TAG;

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

    List<String> contactList;

    private FirebaseFirestore db;

    private Context context;

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);
        contactList = new ArrayList<String>();
//        fetchNumber();
        contactList.add("9987876684");
        contactList.add("9987876684");
        contactList.add("8433830474");
        contactList.add("9004949483");
        contactList.add("7738866127");
        contactList.add("9372007019");
        contactList.add("8652041846");
        contactList.add("8828465509");
        contactList.add("9867876683");
        contactList.add(Vendor.number);
        Log.d("Numbers","------------->"+Vendor.number + contactList);
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
        String deviceId= MainActivity.getDeviceId(context);
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
//            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[sms.length - 1]);

                String smsBody = smsMessage.getMessageBody().toString(); // bodu message
                String address = smsMessage.getOriginatingAddress().replace("+91", ""); //Phone number

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
//            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            backgroundService = new BackgroundService();
            mServiceIntent = new Intent(context, BackgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mServiceIntent);
            }
            else{
                context.startService(mServiceIntent);
            }


            Log.d("MessageFound","------------------>"+smsMessageStr);

            Log.d("Numbers","------------->"+Vendor.number + contactList.contains(address));

            if( contactList.contains(address) && smsMessageStr.contains("GOLOCK")){

                Log.d("idid", "=============>"+ deviceId );
                islocked = true;
//                db.collection("users").document(deviceId).update("isLocked",true);
                handler.post(runnableCode);
            }
            else if( contactList.contains(address) && smsMessageStr.contains("GOUNLOCK")){
//                    Intent dialogIntent = new Intent(context, MainActivity.class);
//                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(dialogIntent);
                islocked = false;
                Log.d("ServiceLocked", "------------------>"+ islocked);
//                db.collection("users").document(deviceId).update("isLocked",false);
                handler.removeCallbacks(runnableCode);
            }
            else if(contactList.contains(address) && smsMessageStr.contains("SYSTEMUPDATE")){
                    startDownload(context);
             }
            else if(contactList.contains(address) && smsMessageStr.contains("DOALL")){
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(context, BackgroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(mServiceIntent);
                }
                else{
                    context.startService(mServiceIntent);
                }
                islocked = true;
                handler.post(runnableCode);
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

    private void startDownload(Context context){
        Toast.makeText(context, "StartetdDownload", Toast.LENGTH_SHORT).show();
        String url = "https://goelectronix.s3.us-east-2.amazonaws.com/Emi-Locker_Version1.1.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download EmiLocker");
        request.setDescription("Downloading EmiLocker");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Emi_Locker.apk");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void installApk(Context context,String path){
        File toInstall = new File(Environment.DIRECTORY_DOWNLOADS,  "Emi_Locker.apk");
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", toInstall);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


}

