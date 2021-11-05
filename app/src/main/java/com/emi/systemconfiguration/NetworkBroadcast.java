package com.emi.systemconfiguration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ServiceManager;
import com.google.firebase.firestore.util.Util;

public class NetworkBroadcast extends BroadcastReciever{
    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService locationService;
    Intent mServiceIntent;

    @Override
    public void onReceive(final Context context, final Intent intent) {


        Log.d("NetworkCheck","+++++++++++++++++++> network");

        String action = intent.getAction();
        if(("android.net.conn.CONNECTIVITY_CHANGE").equals(action) || ("android.net.wifi.WIFI_STATE_CHANGED").contains(action))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mServiceIntent);
                Log.d("NetworStates", "'sdbsdfvdfvdbndsvdsvd Netwrk found");
//                Toast.makeText(context, "Started NEtwpr fore Service", Toast.LENGTH_LONG).show();
            }
            else{
                context.startService(mServiceIntent);
                Log.d("NetworStates", "'sdbsdfvdfvdbndsvdsvd Netwrk found");
//                Toast.makeText(context, "Started NEtw back Service", Toast.LENGTH_LONG).show();
            }
        }


    }


}

