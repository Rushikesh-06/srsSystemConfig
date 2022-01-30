package com.emi.systemconfiguration.provision;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import com.emi.systemconfiguration.DeviceAdmin;
import com.emi.systemconfiguration.common.LaunchIntentUtil;
import com.emi.systemconfiguration.common.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostProvisioningTask {
    private static final String KEY_POST_PROV_DONE = "key_post_prov_done";
    private static final String POST_PROV_PREFS = "post_prov_prefs";
    private static final String SETUP_MANAGEMENT_LAUNCH_ACTIVITY = "com.financelocker.SetupManagementLaunchActivity";
    private static final String TAG = "PostProvisioningTask";
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private final SharedPreferences mSharedPrefs;

    @SuppressLint("WrongConstant")
    public PostProvisioningTask(Context context) {
        this.mContext = context;
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mSharedPrefs = context.getSharedPreferences(POST_PROV_PREFS, 0);
    }

    @SuppressLint("WrongConstant")
    public boolean performPostProvisioningOperations(Intent intent) {
        if (isPostProvisioningDone()) {
            return false;
        }
        markPostProvisioningDone();
        if (Util.SDK_INT >= 23) {
            autoGrantRequestedPermissionsToSelf();
        }
        PersistableBundle persistableBundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            persistableBundle = (PersistableBundle) intent.getParcelableExtra("android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE");
        }
        if (Util.SDK_INT >= 26) {
            maybeSetAffiliationIds(persistableBundle);
        }
        this.mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(this.mContext, SETUP_MANAGEMENT_LAUNCH_ACTIVITY), 2, 1);
        return true;
    }

    public Intent getPostProvisioningLaunchIntent(Intent intent) {
        Intent intent2 = null;
        Account[] accounts;
        String addedAccountName;
        PersistableBundle persistableBundle = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            persistableBundle = (PersistableBundle) intent.getParcelableExtra("android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE");
        }
        String packageName = this.mContext.getPackageName();
        boolean isSynchronousAuthLaunch = LaunchIntentUtil.isSynchronousAuthLaunch(persistableBundle);
//        boolean isCosuLaunch = LaunchIntentUtil.isCosuLaunch(persistableBundle);
        boolean isProfileOwnerApp = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isProfileOwnerApp = this.mDevicePolicyManager.isProfileOwnerApp(packageName);
        }
        boolean isDeviceOwnerApp = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            isDeviceOwnerApp = this.mDevicePolicyManager.isDeviceOwnerApp(packageName);
        }
        if (!isProfileOwnerApp && !isDeviceOwnerApp) {
            return null;
        }
//        if (isCosuLaunch) {
//            intent2 = new Intent(this.mContext, EnableCosuActivity.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                intent2.putExtra("android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE", persistableBundle);
//            }
//        }
//        else {
//            intent2 = new Intent(this.mContext, FinalizeActivity.class);
//        }
//        if (isSynchronousAuthLaunch && (addedAccountName = LaunchIntentUtil.getAddedAccountName(persistableBundle)) != null) {
//            intent2.putExtra(LaunchIntentUtil.EXTRA_ACCOUNT_NAME, addedAccountName);
//        }
//        if (isSynchronousAuthLaunch || isCosuLaunch || (accounts = AccountManager.get(this.mContext).getAccounts()) == null || accounts.length != 0) {
//            intent2.addFlags(268435456);
//            return intent2;
//        }
//        Intent intent3 = new Intent(this.mContext, AddAccountActivity.class);
//        intent3.addFlags(268435456);
//        intent3.putExtra(AddAccountActivity.EXTRA_NEXT_ACTIVITY_INTENT, intent2);
//        return intent3;

        return intent2;
    }

    private void markPostProvisioningDone() {
        this.mSharedPrefs.edit().putBoolean(KEY_POST_PROV_DONE, true).commit();
    }

    private boolean isPostProvisioningDone() {
        return this.mSharedPrefs.getBoolean(KEY_POST_PROV_DONE, false);
    }

    private void maybeSetAffiliationIds(PersistableBundle persistableBundle) {
        String string;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (persistableBundle != null && (string = persistableBundle.getString(LaunchIntentUtil.EXTRA_AFFILIATION_ID)) != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.mDevicePolicyManager.setAffiliationIds(DeviceAdmin.getComponentName(this.mContext), Collections.singleton(string));
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void autoGrantRequestedPermissionsToSelf() {
        String packageName = this.mContext.getPackageName();
        ComponentName componentName = DeviceAdmin.getComponentName(this.mContext);
        for (String next : getRuntimePermissions(this.mContext.getPackageManager(), packageName)) {
            boolean permissionGrantState = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                permissionGrantState = this.mDevicePolicyManager.setPermissionGrantState(componentName, packageName, next, 1);
            }
            Log.d(TAG, "Auto-granting " + next + ", success: " + permissionGrantState);
            if (!permissionGrantState) {
                Log.e(TAG, "Failed to auto grant permission to self: " + next);
            }
        }
    }

    private List<String> getRuntimePermissions(PackageManager packageManager, String str) {
        ArrayList arrayList = new ArrayList();
        try {
            @SuppressLint("WrongConstant") PackageInfo packageInfo = packageManager.getPackageInfo(str, 4096);
            if (!(packageInfo == null || packageInfo.requestedPermissions == null)) {
                for (String str2 : packageInfo.requestedPermissions) {
                    if (isRuntimePermission(packageManager, str2)) {
                        arrayList.add(str2);
                    }
                }
            }
            return arrayList;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not retrieve info about the package: " + str, e);
            return arrayList;
        }
    }

    private boolean isRuntimePermission(PackageManager packageManager, String str) {
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(str, 0);
            if (permissionInfo == null || (permissionInfo.protectionLevel & 15) != 1) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.i(TAG, "Could not retrieve info about the permission: " + str);
        }
        return true;
    }
}
