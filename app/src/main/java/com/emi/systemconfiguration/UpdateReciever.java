package com.emi.systemconfiguration;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import javax.annotation.Nullable;

public class UpdateReciever extends BroadcastReceiver {

    private FirebaseFirestore db;
    String apkversion;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = FirebaseFirestore.getInstance();
        String action = intent.getAction();
        if(("android.intent.action.ACTION_POWER_CONNECTED").equals(action) ||
                ("android.hardware.usb.action.USB_DEVICE_ATTACHED").equals(action) ||
                ("android.intent.action.BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.LOCKED_BOOT_COMPLETED").equals(action) ){
            updateEmiLocker(context, intent);
        }
    }

    private void updateEmiLocker(Context context, Intent intent){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            apkversion = pInfo.versionName;

            DocumentReference documentReference = db.collection("update").document("sjVCd1oyiDUZDTBa04qD");
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        // this method is called when error is not null
                        // and we gt any error
                        // in this cas we are displaying an error message.
                        Log.d("Error is", "Error found" + e);

                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        Boolean status = (Boolean) documentSnapshot.getData().get("status");
                        String version =(String) documentSnapshot.getData().get("version");
                        Log.d("UpdatedRc", status.toString());
                        Toast.makeText(context, "StartedStatus" + status + version + "=" + apkversion, Toast.LENGTH_SHORT).show();
                        if(status && version.equals(apkversion)){
                            DownloadApk(context);
                        }
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private  void DownloadApk(Context context){
        Toast.makeText(context, "Started to Download", Toast.LENGTH_SHORT).show();
        String url = "https://goelectronix.s3.us-east-2.amazonaws.com/Emi-Locker_Version1.1.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Downloading System update");
        request.setDescription("System Update");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Emi_Locker.apk");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Objects.requireNonNull(manager).enqueue(request);
        Log.d("DownloadStatus", String.valueOf(DownloadManager.STATUS_SUCCESSFUL));

        if(DownloadManager.STATUS_SUCCESSFUL == 8){
            Log.d("Success", "DOwnloaded Successfully");
            installApk(context);
        }

    }

    private void installApk(Context context) {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PackageInstaller pi = context.getPackageManager().getPackageInstaller();
                int sessId = pi.createSession(new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));
                PackageInstaller.Session session = pi.openSession(sessId);

                // .. write updated APK file to out
                long sizeBytes = 0;
                final File file = new File("/storage/emulated/0/Download/Emi_Locker.apk");
                if (file.isFile()) {
                    sizeBytes = file.length();
                }
                InputStream in = null;
                OutputStream out = null;
                in = new FileInputStream("/storage/emulated/0/Download/Emi_Locker.apk");
                out = session.openWrite("my_app_session", 0, sizeBytes);

                int total = 0;
                byte[] buffer = new byte[65536];
                int c;
                while ((c = in.read(buffer)) != -1) {
                    total += c;
                    out.write(buffer, 0, c);
                }

                session.fsync(out);

                in.close();
                out.close();

                System.out.println("InstallApkViaPackageInstaller - Success: streamed apk " + total + " bytes");
                // fake intent
                Context app = context;
                Intent intent = new Intent(app, DeviceAdmin.class);
                PendingIntent alarmtest = PendingIntent.getBroadcast(app,
                        1337111117, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                session.commit(alarmtest.getIntentSender());
                session.close();

                boolean deleted = file.delete();
                Log.d("FileDelete", deleted+"DeletedFIles is succcess");
//                Toast.makeText(context, "Deleted the File", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            Log.d("InstallError", ex.toString());
            ex.printStackTrace();
        }

    }
}
