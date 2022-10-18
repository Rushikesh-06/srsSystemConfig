package com.emi.systemconfiguration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LauncherReceiver  extends BroadcastReceiver {
    public static final String START_INTENT = "com.aaa.aaa.action.START";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("CALL RECEIVER!!");
        Log.d("errp", "CALL RECEIVER");
        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(startIntent);


    }
}
