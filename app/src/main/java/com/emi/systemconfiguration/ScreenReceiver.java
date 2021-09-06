package com.emi.systemconfiguration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class ScreenReceiver extends BroadcastReceiver {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String
            SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
            String dialogType = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if(dialogType != null && dialogType.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)){
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(closeDialog);
            }

        }

        GlobalLock globalLock = com.emi.systemconfiguration.GlobalLock.getInstance();
        if(globalLock.getData()){
            Toast.makeText(context, "True Details Lock",Toast.LENGTH_LONG).show();
            Log.d("Context","------------*");
        }
        else {
            Toast.makeText(context, " Lock",Toast.LENGTH_LONG).show();
            Log.d("ContextFalse","------------*");
        }

    }

}