package com.emi.systemconfiguration;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.FileOutputStream;
import java.io.IOException;

public class FirebaseMessageReceiver extends FirebaseMessagingService {


    private String TAG= getClass().getSimpleName();

    Context context = getApplicationContext();
    private String filename = "q1w2e3r4t5y6u7i8o9p0.txt";
    private BackgroundService backgroundService;
    Intent mServiceIntent;
    private UninstallService uninstallService;
    Intent getServiceIntent;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(context, FirebaseMessageReceiver.class);

        String deviceId= MainActivity.getDeviceId(context);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getFrom().equalsIgnoreCase("741955552131")){
            if (remoteMessage.getData().containsKey("command")) {
                if (remoteMessage.getData().get("command").equals("GOLOCK")){
                    Log.d("idid", "=============>"+ deviceId );
                    boolean islocked = true;
                    Intent dialogIntent = new Intent(context, EmiDueDate.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dialogIntent);
                    writeData("true",context);
                }else if (remoteMessage.getData().get("command").equals("GOUNLOCK")){
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(context);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "com.emi.action.unlock"));
                    boolean islocked = false;
                    Log.d("ServiceLocked", "------------------>"+ islocked);

                    startService(context, null);
                    try{
                        writeData("false",context);
                    }
                    catch(Exception e){
                        Log.d("Ee", "exc" + e);
                    }
                }
            }
        }


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

          /*  if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void startService(Context context, Intent intent){
        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(context, backgroundService.getClass());
        context.startService(mServiceIntent);
        uninstallService = new UninstallService();
        getServiceIntent = new Intent(context, uninstallService.getClass());
        context.startService(getServiceIntent);
    }

    private void writeData(String status,Context context)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            String data = status;
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}