package com.emi.systemconfiguration.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebasePushNotification extends FirebaseMessagingService {

    private  String TAG = getClass().getSimpleName() ;

    public FirebasePushNotification(){
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.e(TAG,message.toString());
    }
}
