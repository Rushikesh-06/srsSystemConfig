package com.emi.systemconfiguration;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DownloadBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            String downloadPath = intent.getStringExtra(DownloadManager.COLUMN_URI);
//            Toast.makeText(context,downloadPath,Toast.LENGTH_LONG).show();
        }
    }
}
