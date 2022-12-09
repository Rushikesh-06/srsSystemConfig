package com.emi.systemconfiguration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class BroadcastReciever extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private UninstallService uninstallService;
    Intent mServiceIntent;
    Intent getmServiceIntent;


    public ComponentName mDeviceAdmin;
    DevicePolicyManager dpm;

    Boolean screenOff;
    SharedPreferences sharedPreferences;
    private final String filename = "q1w2e3r4t5y6u7i8o9p0.txt";

    Context context1;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        sharedPreferences = context.getSharedPreferences("LockingState", Context.MODE_PRIVATE);
        Boolean status = sharedPreferences.getBoolean("status", false);
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        context1 = context;

        if (("android.intent.action.BOOT_COMPLETED").equals(action) ||
                ("restart.service").contains(action) ||
                ("android.intent.action.ACTION_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.ACTION_LOCKED_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.QUICKBOOT_POWERON").equals(action) ||
                ("android.intent.action.ACTION_POWER_CONNECTED").equals(action) ||
                ("android.hardware.usb.action.USB_DEVICE_ATTACHED").equals(action) ||
                ("android.intent.action.LOCKED_BOOT_COMPLETED").equals(action) ||
                ("android.intent.action.BATTERY_CHANGED").contains(action) ||
                ("android.intent.action.BATTERY_STATUS_CHARGING").contains(action) ||
                ("android.intent.action.ACTION_POWER_DISCONNECTED").contains((action)) ||
                ("android.intent.action.PACKAGE_REMOVED").contains((action)) ||
                ("android.intent.action.ACTION_SHUTDOWN").contains((action)) ||
                ("android.intent.action.AIRPLANE_MODE").contains((action)) ||
                ("android.intent.action.SCREEN_ON").contains((action)) ||
                ("android.intent.action.SCREEN_OFF").contains((action)) ||
                ("android.intent.action.CONFIGURATION_CHANGED").contains((action)) ||
                ("android.intent.action.REBOOT").contains((action)) ||
                ("BackgroundProcess").equals(action) ||
                ("android.app.action.DEVICE_ADMIN_ENABLED").equals(action)) {


            SharedPreferences sharedPreferences = context.getSharedPreferences("LockingState", Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean("status", false)) {
                Intent dialogIntent = new Intent(context, EmiDueDate.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialogIntent);
            }

            //api call cst status sync passing status

            JSONObject sync_params = new JSONObject();
            try {
                try {
                    sync_params.put("SerialNumber", Build.getSerial());
                }catch (Exception e){
                    Toast.makeText(context, "Error getting Serial Number.", Toast.LENGTH_SHORT).show();
                }
//                sync_params.put("IMEINumber", );

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

                    Log.d("Test", "Current list = " + subsInfoList);

                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                        if (sync_params.has("SimNumber1")) {
                            sync_params.put("SimNumber2", subscriptionInfo.getNumber());
                        } else {
                            sync_params.put("SimNumber1", subscriptionInfo.getNumber());
                        }

                        String number = subscriptionInfo.getNumber();

                        Log.d("Test", " Number is  " + number);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest sync_api_status = new JsonObjectRequest(Request.Method.POST, "http://goelectronix.in/api/app/CustomerStatusSync", sync_params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.e("response", response.toString());
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("error", error.getMessage());
                }
            });

            Volley.newRequestQueue(context).add(sync_api_status);


//            startService();
/*
            try {
                try {
                    if (Objects.requireNonNull(readData(context)).equals("true")) {
                        Log.d("---------->d1", readData(context));
                        backgroundService = new BackgroundService();
                        mServiceIntent = new Intent(context, BackgroundService.class);
                        context.startService(mServiceIntent);

                    }
                } catch (Exception e) {
                    Log.d("Ex", "Ex" + e);

                }
                if (status) {
//                    dpm.lockNow();
                    backgroundService = new BackgroundService();
                    mServiceIntent = new Intent(context, BackgroundService.class);
                    context.startService(mServiceIntent);
                    Intent dialogIntent = new Intent(context, EmiDueDate.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dialogIntent);

                }


            } catch (Exception e) {
                Log.d("Err", "Error" + e);
            }
*/

        }
    }
}
