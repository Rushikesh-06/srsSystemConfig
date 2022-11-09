package com.emi.systemconfiguration.Firebase;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends com.google.firebase.messaging.FirebaseMessagingService {

    private  String TAG = getClass().getSimpleName() ;

    public FirebaseMessageReceiver(){
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.e(TAG,message.toString());
    }
}
