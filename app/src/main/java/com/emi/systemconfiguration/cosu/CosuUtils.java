package com.emi.systemconfiguration.cosu;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Handler;

class CosuUtils {
    public static final boolean DEBUG = false;
    private static final int DOWNLOAD_TIMEOUT_MILLIS = 120000;
    public static final int MSG_DOWNLOAD_COMPLETE = 1;
    public static final int MSG_DOWNLOAD_TIMEOUT = 2;
    public static final int MSG_INSTALL_COMPLETE = 3;
    public static final String TAG = "CosuSetup";

    CosuUtils() {
    }

    public static Long startDownload(DownloadManager downloadManager, Handler handler, String str) {
        Long valueOf = Long.valueOf(downloadManager.enqueue(new DownloadManager.Request(Uri.parse(str))));
        handler.sendMessageDelayed(handler.obtainMessage(2, valueOf), 120000);
        return valueOf;
    }
}
