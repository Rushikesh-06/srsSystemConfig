package com.emi.systemconfiguration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.common.util.concurrent.ServiceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.util.Util;

public class NetworkBroadcast extends BroadcastReciever{
    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    private static final String tag = "TestReceiver";
    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    Intent mServiceIntent;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String deviceId = MainActivity.getDeviceId(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Log.d("NetworkCheck","+++++++++++++++++++> network");

        String action = intent.getAction();
        if(("android.net.conn.CONNECTIVITY_CHANGE").equals(action) || ("android.net.wifi.WIFI_STATE_CHANGED").contains(action))


        Toast.makeText(context, context.getPackageName(), Toast.LENGTH_SHORT).show();
        // Says "bar" every half second

    }




}

