package com.emi.systemconfiguration;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UpdateService extends Service {
    DevicePolicyManager dpm;
    private FirebaseFirestore db;
    String apkversion;
    String generatedString;

    @Override
    public void onCreate() {
        super.onCreate();
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        updateEmiLocker(this, intent);
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateEmiLocker(Context context, Intent intent){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            apkversion = pInfo.versionName;

            DocumentReference documentReference = db.collection("update").document("sjVCd1oyiDUZDTBa04qD");
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
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
                        String fileUrl = (String) documentSnapshot.getData().get("url");
                        Log.d("UpdatedRc", status.toString());
                      //  Toast.makeText(context, "StartedStatus" + status + version + "=" + apkversion, Toast.LENGTH_SHORT).show();
                        if(status && !apkversion.equals(version)){
                            DownloadApk(context, fileUrl);
                        }
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private  void DownloadApk(Context context, String url){
        try{
//            Toast.makeText(context, "StartetdDownload", Toast.LENGTH_SHORT).show();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Download EmiLocker");
            request.setDescription("Downloading EmiLocker");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            generatedString = MainActivity.getSaltString(9);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,generatedString+".apk");
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            //Registering receiver in Download Manager
            context.getApplicationContext().registerReceiver(onCompleted, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            manager.enqueue(request);
        }
        catch(Exception e){
            Log.d("Error---", e+"jf");

        }

    }


    BroadcastReceiver onCompleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    File root=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS.toString() + "/"+generatedString+".apk");
                    InputStream inputStream =new FileInputStream(root.getAbsolutePath());
                    PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
                    int sessionId = 0;
                    sessionId = packageInstaller.createSession(new PackageInstaller
                            .SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));

                    PackageInstaller.Session session = packageInstaller.openSession(sessionId);

                    long sizeBytes = 0;

                    OutputStream out = null;
                    out = session.openWrite("my_app_session", 0, sizeBytes);

                    int total = 0;
                    byte[] buffer = new byte[65536];
                    int c;
                    while ((c = inputStream.read(buffer)) != -1) {
                        total += c;
                        out.write(buffer, 0, c);
                    }
                    session.fsync(out);
                    inputStream.close();
                    out.close();

                    session.commit(createIntentSender(context,sessionId));

                }
            }
            catch(Exception e){

                Log.d("errp", e+"dfhfdh"+ Environment.DIRECTORY_DOWNLOADS );
            }
        }
    };

    private IntentSender createIntentSender(Context context, int sessionId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(LauncherReceiver.START_INTENT),
                0);
//
//        File root=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS.toString() + "/"+generatedString+".apk");
//        root.delete();

        return pendingIntent.getIntentSender();
    }


}
