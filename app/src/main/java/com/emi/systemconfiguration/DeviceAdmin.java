package com.emi.systemconfiguration;

import android.app.admin.DeviceAdminReceiver;

import android.content.Context;

import android.content.Intent;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class DeviceAdmin extends DeviceAdminReceiver {

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Device admin enabled");

    }
    @Override
    public void onDisabled(Context context, Intent intent) {

        Log.d("note", "going to main from disabled admin");

    }
    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        Log.d("Device Admin","Disable Requested");


        return "Warning";

    }

}

