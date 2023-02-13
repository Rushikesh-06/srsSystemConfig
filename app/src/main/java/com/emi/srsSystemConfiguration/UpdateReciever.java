package com.emi.srsSystemConfiguration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.FirebaseFirestore;

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
