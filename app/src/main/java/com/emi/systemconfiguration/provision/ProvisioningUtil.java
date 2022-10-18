package com.emi.systemconfiguration.provision;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.emi.systemconfiguration.DeviceAdmin;


public class ProvisioningUtil {
    public static void enableProfile(Context context) {
        @SuppressLint("WrongConstant") DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        ComponentName componentName = DeviceAdmin.getComponentName(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            devicePolicyManager.setProfileName(componentName,"hhds" );
            devicePolicyManager.setProfileEnabled(componentName);
        }

    }
}
