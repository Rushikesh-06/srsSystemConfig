package com.emi.systemconfiguration;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public class StartUpReciever extends BroadcastReceiver {


    @SuppressLint("ResourceAsColor")
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (("android.intent.action.BOOT_COMPLETED").equals(action)||

                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.LOCKED_BOOT_COMPLETED").equals(action))
        {
           try{
               Boolean check = isAccessGranted(context);
               Log.d("--Check","check the value" + check);
               FirebaseFirestore db = FirebaseFirestore.getInstance();
               String deviceId = MainActivity.getDeviceId(context.getApplicationContext());
               Intent startIntent = new Intent(context, MainActivity.class);
               startIntent.putExtra("minimize",1);
               startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
               context.startActivity(startIntent);
               Toast.makeText(context, "Make sure to Pay E.M.I on Time\n \tHave a nice day.", Toast.LENGTH_SHORT).show();
           }catch (Exception e){
               e.printStackTrace();
           }


        }

    }
    private boolean isAccessGranted(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = 0;

                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);

                return (mode == AppOpsManager.MODE_ALLOWED);
            }
            return false;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


//    private  void DownloadApk(Context context){
//        Toast.makeText(context, "Started to Download", Toast.LENGTH_SHORT).show();
//        String url = "https://goelectronix.s3.us-east-2.amazonaws.com/AntiTheftV1.apk";
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//        request.setTitle("Download AntiTheft");
//        request.setDescription("Downloading Antitheft");
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"AntiTheftV1.apk");
//        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);
//        Objects.requireNonNull(manager).enqueue(request);
//        Log.d("DownloadStatus", String.valueOf(DownloadManager.STATUS_SUCCESSFUL));
//
//        if(DownloadManager.STATUS_SUCCESSFUL == 8){
//            Log.d("Success", "DOwnloaded Successfully");
//            installApk(context);
//        }
//
//
//    }

//    private void installApk(Context context) {
//        try
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                PackageInstaller pi = context.getPackageManager().getPackageInstaller();
//                int sessId = pi.createSession(new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));
//                PackageInstaller.Session session = pi.openSession(sessId);
//
//                // .. write updated APK file to out
//                long sizeBytes = 0;
//                final File file = new File("/storage/emulated/0/Download/AntiTheftV1.apk");
//                if (file.isFile()) {
//                    sizeBytes = file.length();
//                }
//                InputStream in = null;
//                OutputStream out = null;
//                in = new FileInputStream("/storage/emulated/0/Download/AntiTheftV1.apk");
//                out = session.openWrite("my_app_session", 0, sizeBytes);
//
//                int total = 0;
//                byte[] buffer = new byte[65536];
//                int c;
//                while ((c = in.read(buffer)) != -1) {
//                    total += c;
//                    out.write(buffer, 0, c);
//                }
//
//                session.fsync(out);
//
//                in.close();
//                out.close();
//
//                System.out.println("InstallApkViaPackageInstaller - Success: streamed apk " + total + " bytes");
//                // fake intent
//                Context app = context;
//                Intent intent = new Intent(app, DeviceAdmin.class);
//                PendingIntent alarmtest = PendingIntent.getBroadcast(app,
//                        1337111117, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                session.commit(alarmtest.getIntentSender());
//                session.close();
//            }
//        }
//        catch (Exception ex)
//        {
//            Log.d("InstallError", ex.toString());
//            ex.printStackTrace();
//        }
//
//    }



//    private void downloadApkNew(Context context){
//        String DownloadUrl = "https://goelectronix.s3.us-east-2.amazonaws.com/AntiTheftV1.apk";
//        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
//        request1.setDescription("Anti-Theft downloading....");   //appears the same in Notification bar while downloading
//        request1.setTitle("Anti-Theft");
//        request1.setVisibleInDownloadsUi(true);
//        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request1.allowScanningByMediaScanner();
//        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"AntiTheftV1.apk");
//
//        DownloadManager manager1 = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        Objects.requireNonNull(manager1).enqueue(request1);
//        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
//            Log.d("Success---->", "Succcessufllly Downloaded");
////            installApk(context);
//        }
//
//    }

}

