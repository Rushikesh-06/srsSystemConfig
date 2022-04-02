package com.emi.systemconfiguration;

import android.app.admin.DeviceAdminReceiver;

import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.emi.systemconfiguration.provision.PostProvisioningTask;


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

    public void onProfileProvisioningComplete(Context context, Intent intent) {
        PostProvisioningTask postProvisioningTask = new PostProvisioningTask(context);
        if (postProvisioningTask.performPostProvisioningOperations(intent)) {
            Intent postProvisioningLaunchIntent = postProvisioningTask.getPostProvisioningLaunchIntent(intent);
            if (postProvisioningLaunchIntent != null) {
                context.startActivity(postProvisioningLaunchIntent);
                return;
            }
            Log.e("txt", "DeviceAdminReceiver.onProvisioningComplete() invoked, but ownership not assigned");
//            Toast.makeText(context, C0740R.string.device_admin_receiver_failure, 1).show();
        }
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }

}

