package com.emi.srsSystemConfiguration;

import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;

public class FirebaseMessageReceiver extends FirebaseMessagingService {


    private String TAG = getClass().getSimpleName();
    public boolean islocked;
    Context context;
    SharedPreferences sharedPreferences;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        context = getApplicationContext();
//        backgroundService = new BackgroundService();
        sharedPreferences = getSharedPreferences("LockingState", MODE_PRIVATE);

        String deviceId = MainActivity.getDeviceId(context);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
//        Toast.makeText(context, "Test Success.", Toast.LENGTH_SHORT).show();
        if (remoteMessage.getData().containsKey("command")) {
            Log.e(TAG, "onMessageReceived: "+remoteMessage.getData().get("command") );
//            Toast.makeText(context, remoteMessage.getData().get("command"), Toast.LENGTH_SHORT).show();
        }

        if (remoteMessage.getFrom().equalsIgnoreCase("741955552131")) {
            if (remoteMessage.getData().containsKey("command")) {
                if (remoteMessage.getData().get("command").equals("GOLOCK")) {
                    Log.d("idid", "=============>" + deviceId);
                    islocked = true;
                    Intent dialogIntent = new Intent(context, EmiDueDate.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dialogIntent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("status", true);
                    editor.apply();
                } else if (remoteMessage.getData().get("command").equals("GOUNLOCK")) {
                    SharedPreferences preferences = getSharedPreferences("EMILOCKER", MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = preferences.edit();
                    editor2.putString("displayName", remoteMessage.getData().get("displayName"));
                    editor2.putString("displayContactNumber", remoteMessage.getData().get("displayContactNumber"));
                    editor2.putString("paymentNumber", remoteMessage.getData().get("paymentNumber"));
                    editor2.commit();
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(context);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "com.emi.action.unlock"));
                    boolean islocked = false;
                    Log.d("ServiceLocked", "------------------>" + islocked);


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("status", false);
                    editor.apply();
                } else if (remoteMessage.getData().get("command").equals("SYSTEMUPDATE")) {
                    startDownload(context);
                } else if (remoteMessage.getData().get("command").equals("GETDETAILS")) {
                    Intent intent = new Intent(context, BroadcastReciever.class);
                    intent.setAction("BackgroundProcess");
                    sendBroadcast(intent);
                } else if (remoteMessage.getData().get("command").equals("UNINSTALLAPP")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                        devicePolicyManager.clearDeviceOwnerApp(context.getPackageName());
                        devicePolicyManager.setUninstallBlocked(new ComponentName(getApplicationContext(), DeviceAdmin.class), getPackageName(), false);
                    }
                }
            }
        }


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            /*  if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void startDownload(Context context) {
        Toast.makeText(context, "StartetdDownload", Toast.LENGTH_SHORT).show();
        String url = "https://downloadzone.s3.ap-south-1.amazonaws.com/Emi-Locker_Version1.1.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download EmiLocker");
        request.setDescription("Downloading EmiLocker");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Emi_Locker.apk");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        try {
            installApk(context, request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Emi_Locker.apk"));
        } catch (Exception e) {
            Log.d("errp", e + "dfhfdh");
        }
    }

    private void installApk(Context context, DownloadManager.Request path) {
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


}