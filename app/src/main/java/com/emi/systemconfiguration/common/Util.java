package com.emi.systemconfiguration.common;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.IntentCompat;
import androidx.preference.PreferenceFragment;

import  com.emi.systemconfiguration.DeviceAdmin;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class Util {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final boolean IS_RUNNING_R;
    public static final int Q_VERSION_CODE = 29;
    public static final int R_VERSION_CODE = 30;
    public static final int SDK_INT;
    private static final String TAG = "Util";

    static {
        int i;
        boolean z = false;
        if (Build.VERSION.CODENAME.length() == 1 && Build.VERSION.CODENAME.charAt(0) == 'R') {
            z = true;
        }
        IS_RUNNING_R = z;
        if (z) {
            i = 10000;
        } else {
            i = Build.VERSION.SDK_INT;
        }
        SDK_INT = i;
    }

    public static CharSequence formatTimestamp(long j) {
        if (j == 0) {
            return null;
        }
        return DateUtils.formatSameDayTime(j, System.currentTimeMillis(), 2, 1);
    }

//    public static void updateImageView(Context context, ImageView imageView, Uri uri) {
//        try {
//            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(openInputStream, (Rect) null, options);
//            int i = options.outWidth;
//            int i2 = options.outHeight;
//            int max = Math.max(i / imageView.getMaxWidth(), i2 / imageView.getMaxHeight());
//            if (max > 1) {
//                options.inSampleSize = max;
//            }
//            options.inJustDecodeBounds = false;
//            imageView.setImageBitmap(BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), (Rect) null, options));
//        } catch (FileNotFoundException unused) {
////            Toast.makeText(context, C0740R.string.error_opening_image_file, 0);
//        }
//    }

    public static boolean isManagedProfileOwner(Context context) {
        DevicePolicyManager devicePolicyManager = getDevicePolicyManager(context);
        if (SDK_INT < 24) {
            return isProfileOwner(context);
        }
        try {
            return devicePolicyManager.isManagedProfile(DeviceAdmin.getComponentName(context));
        } catch (SecurityException unused) {
            return false;
        }
    }
//
//    public static boolean isPrimaryUser(Context context) {
//        if (SDK_INT >= 23) {
//            return ((UserManager) context.getSystemService("user")).isSystemUser();
//        }
//        return isDeviceOwner(context);
//    }

//    public static boolean isDeviceOwner(Context context) {
//        return getDevicePolicyManager(context).isDeviceOwnerApp(context.getPackageName());
//    }

    public static boolean isProfileOwner(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDevicePolicyManager(context).isProfileOwnerApp(context.getPackageName());
        }
        return true;
    }

    public static List<UserHandle> getBindDeviceAdminTargetUsers(Context context) {
        if (SDK_INT < 26) {
            return Collections.emptyList();
        }
        return getDevicePolicyManager(context).getBindDeviceAdminTargetUsers(DeviceAdmin.getComponentName(context));
    }

//    public static void showFileViewer(PreferenceFragment preferenceFragment, int i) {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setTypeAndNormalize("*/*");
//        try {
//            preferenceFragment.startActivityForResult(intent, i);
//        } catch (ActivityNotFoundException e) {
//            Log.e(TAG, "showFileViewer: ", e);
//        }
//    }

//    public static boolean installCaCertificate(InputStream inputStream, DevicePolicyManager devicePolicyManager, ComponentName componentName) {
//        if (inputStream != null) {
//            try {
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                byte[] bArr = new byte[4096];
//                while (true) {
//                    int read = inputStream.read(bArr);
//                    if (read <= 0) {
//                        return devicePolicyManager.installCaCert(componentName, byteArrayOutputStream.toByteArray());
//                    }
//                    byteArrayOutputStream.write(bArr, 0, read);
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "installCaCertificate: ", e);
//            }
//        }
//        return false;
//    }

//    public static Intent getHomeIntent() {
//        Intent intent = new Intent("android.intent.action.MAIN");
//        intent.addFlags(268435456);
//        intent.addCategory("android.intent.category.HOME");
//        return intent;
//    }

    public static IntentFilter getHomeIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        intentFilter.addCategory("android.intent.category.HOME");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        return intentFilter;
    }

    public static Intent getLauncherIntent(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        if (isRunningOnTvDevice(context)) {
            intent.addCategory(IntentCompat.CATEGORY_LEANBACK_LAUNCHER);
        } else {
            intent.addCategory("android.intent.category.LAUNCHER");
        }
        return intent;
    }

    @SuppressLint("WrongConstant")
    private static DevicePolicyManager getDevicePolicyManager(Context context) {
        return (DevicePolicyManager) context.getSystemService("device_policy");
    }

    public static boolean hasDelegation(Context context, String str) {
        if (SDK_INT < 26) {
            return false;
        }
        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getDelegatedScopes((ComponentName) null, context.getPackageName()).contains(str);
    }

    @SuppressLint("WrongConstant")
    public static boolean isRunningOnTvDevice(Context context) {
        return ((UiModeManager) context.getSystemService("uimode")).getCurrentModeType() == 4;
    }
}
