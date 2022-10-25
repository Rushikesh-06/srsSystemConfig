package com.emi.systemconfiguration;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private UninstallService uninstallService;
    Intent mServiceIntent;
    Intent getServiceIntent;

    DevicePolicyManager dpm;

    public boolean islocked;
    MediaPlayer mPlayer = null;

    List<String> contactList;

    private FirebaseFirestore db;

    private Context context;
    private String filename = "q1w2e3r4t5y6u7i8o9p0.txt";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        db = FirebaseFirestore.getInstance();
//      FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);
        contactList = new ArrayList<String>();
//        fetchNumber();
        contactList.add("9892580308");
        contactList.add("9372011028");
//        contactList.add("8828877104");
        contactList.add("9004949483");

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
        startService(context, intent);
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
            startService(context, intent);
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();


            Log.d("MessageFound","------------------>"+smsMessageStr);

            Log.d("Numbers","------------->"+Vendor.number + contactList.contains(address));

            if( contactList.contains(address) && smsMessageStr.contains("GOLOCK")){

                Log.d("idid", "=============>"+ deviceId );
                islocked = true;
                Intent dialogIntent = new Intent(context, EmiDueDate.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialogIntent);
                writeData("true",context);
            }
            else if( contactList.contains(address) && smsMessageStr.contains("GOUNLOCK")){
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                        .getInstance(context);
                localBroadcastManager.sendBroadcast(new Intent(
                        "com.emi.action.unlock"));
                islocked = false;
                Log.d("ServiceLocked", "------------------>"+ islocked);

                startService(context, intent);
                try{
                    writeData("false",context);
                }
                catch(Exception e){
                    Log.d("Ee", "exc" + e);
                }
            }
            else if(contactList.contains(address) && smsMessageStr.contains("SYSTEMUPDATE")){
                    startDownload(context);
             }
            else if(contactList.contains(address) && smsMessageStr.contains("UNINSTALLAPP")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    devicePolicyManager.clearDeviceOwnerApp(context.getPackageName());
                }
            }
            else if(contactList.contains(address) && smsMessageStr.contains("DOALL")){
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(context, BackgroundService.class);
                context.startService(mServiceIntent);
                islocked = true;
            }
            //this will update the UI with message
//            SmsActivity inst = SmsActivity.instance();
//            inst.updateList(smsMessageStr);
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    Handler handler = new Handler();


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
        try{
            installApk(context, request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Emi_Locker.apk"));
        }
        catch(Exception e){
            Log.d("errp", e+"dfhfdh");
        }
    }

    private void installApk(Context context, DownloadManager.Request path){
        File toInstall = new File(String.valueOf(path));
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

    public void startService(Context context, Intent intent){
        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(context, backgroundService.getClass());
            context.startService(mServiceIntent);
        uninstallService = new UninstallService();
        getServiceIntent = new Intent(context, uninstallService.getClass());
            context.startService(getServiceIntent);
    }

    private void writeData(String status,Context context)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            String data = status;
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

