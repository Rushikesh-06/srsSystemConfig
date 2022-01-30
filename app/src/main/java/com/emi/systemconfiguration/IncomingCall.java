package com.emi.systemconfiguration;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class IncomingCall extends BroadcastReceiver {

    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private UninstallService uninstallService;
    Intent mServiceIntent;
    Intent getmServiceIntent;
    Context myContext;

    Boolean screenOff;
    DevicePolicyManager dpm;
    SharedPreferences sharedPreferences ;
    @Override
    public void onReceive(Context context, Intent intent) {

        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        try {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context,"Getting Call from  -"+incomingNumber,Toast.LENGTH_SHORT).show();
                Log.d("callfrom","Getting Call from  -"+incomingNumber );
                startService(context,intent);

            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                Log.d("Call", "Activity");
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();
                Log.d("Call", "Activity");
                sharedPreferences = context. getSharedPreferences("LockingState",Context.MODE_PRIVATE);
                Boolean status = sharedPreferences.getBoolean("status", false);

                if(status) {
                    dpm.lockNow();
                    startService(context,intent);
                }
            }


        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    public void startService(Context context, Intent intent){
        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(context, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mServiceIntent);
        }
        else{
            context.startService(mServiceIntent);
        }

        uninstallService = new UninstallService();
        getmServiceIntent = new Intent(context, uninstallService.getClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(getmServiceIntent);
        }
        else{
            context.startService(getmServiceIntent);
        }
    }
}
