package com.emi.systemconfiguration.Firebase;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by softhubtech on 09/03/18.
 */

public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService
{
    private String TAG= getClass().getSimpleName();
    RequestQueue queue;


    @Override
    public void onTokenRefresh()
    {
        queue = Volley.newRequestQueue(getApplicationContext());
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic("global");

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }

}