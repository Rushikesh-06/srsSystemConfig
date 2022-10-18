package com.emi.systemconfiguration;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
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
    String generatedString;

    private UpdateService updateService;
    Intent mServiceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        db = FirebaseFirestore.getInstance();
        String action = intent.getAction();
        if (("android.intent.action.ACTION_POWER_CONNECTED").equals(action) ||
                ("android.hardware.usb.action.USB_DEVICE_ATTACHED").equals(action) ||
                ("android.intent.action.BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.LOCKED_BOOT_COMPLETED").equals(action)) {
            updateService = new UpdateService();
            mServiceIntent = new Intent(context, UpdateService.class);
            context.startService(mServiceIntent);
        }
    }
}
