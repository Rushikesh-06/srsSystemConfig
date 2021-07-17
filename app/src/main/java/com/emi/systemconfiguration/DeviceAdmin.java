package com.emi.systemconfiguration;


import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DeviceAdminService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;

import android.os.UserManager;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.admin.DevicePolicyManager.*;
import static android.content.ContentValues.TAG;

public class DeviceAdmin extends DeviceAdminReceiver {
    DevicePolicyManager dpm;
    long current_time;
    Timer myThread;
    private Context context;
//    DevicePolicyManager devicePolicyManager =
//            (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    private static final String OUR_SECURE_ADMIN_PASSWORD = "12345";

    //    USb Disable
    private static final String TAG = "ShutDownReceiver";
    private boolean mReady = false;
    private Context  mContext;
    private final HashMap<String, String> mVolumeStates = new HashMap<String, String>();
    private boolean  mUmsEnabling;
    public static final String DISALLOW_USB_FILE_TRANSFER = "false";



    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.v("PlugInControlReceiver","action: "+action);
        String state = Environment.getExternalStorageState();
        Intent startMain = new Intent(context, MainActivity.class);
//                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startMain.putExtra("flag","disable");
        context.startActivity(startMain);
//        showToast(context, "Device admin disabled");

    }


    private void waitForReady() {
        while (mReady == false) {
            for (int retries = 5; retries > 0; retries--) {
                if (mReady) {
                    return;
                }
                SystemClock.sleep(1000);
            }
            Log.v(TAG, "Waiting too long for mReady!");
        }
    }


    /**
     * @return state of the volume at the specified mount point
     */

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Device admin enabled");
    }
    @Override
    public void onDisabled(Context context, Intent intent) {

        Log.d("note", "going to main from disabled admin");
        Intent startMain = new Intent(context, MainActivity.class);
//                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.putExtra("flag","disable");
        context.startActivity(startMain);
        showToast(context, "Device admin disabled");
    }
    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        Log.d("Device Admin","Disable Requested");
        this.context = context;

        Intent startSetting = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        startSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startSetting.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(startSetting);



        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        myThread = new Timer();
        current_time = System.currentTimeMillis();
        myThread.schedule(lock_task,0,1000);

        return "Warning";


    }

    // Repeatedly lock the phone every second for 5 seconds
    TimerTask lock_task = new TimerTask() {
        @Override
        public void run() {
            long diff = System.currentTimeMillis() - current_time;
            if (diff<5000) {
                Log.d("Timer","1 second");
                dpm.lockNow();
            }
            else{

                Log.d("note", "going to main from disable requested");
                Intent startMain = new Intent(context, MainActivity.class);
//                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startMain.putExtra("flag","disable");
                context.startActivity(startMain);

//                context.startService(new Intent(context, BackgroundService.class));

//                Toast.makeText(context,"onCreate", Toast.LENGTH_LONG).show();
//                HUDView mView = new HUDView(this);
//                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                        PixelFormat.TRANSLUCENT);
//                params.gravity = Gravity.RIGHT | Gravity.TOP;
//                params.setTitle("Load Average");
//                WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//                wm.addView(mView, params);
                myThread.cancel();
            }
        }
    };
}

