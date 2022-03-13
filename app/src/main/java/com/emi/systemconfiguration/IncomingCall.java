package com.emi.systemconfiguration;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IncomingCall extends BroadcastReceiver {

    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private UninstallService uninstallService;
    Intent mServiceIntent;
    Intent getmServiceIntent;
    Context myContext;
    List<String> contactList;

    Boolean screenOff;
    DevicePolicyManager dpm;
    SharedPreferences sharedPreferences ;
    private String filename = "q1w2e3r4t5y6u7i8o9p0.txt";

    @Override
    public void onReceive(Context context, Intent intent) {

        contactList = new ArrayList<String>();
        contactList.add("9987876684");
        contactList.add("9987876684");
        contactList.add("8433830474");
        contactList.add("9004949483");
        contactList.add("7738866127");
        contactList.add("9372007019");
        contactList.add("8652041846");
        contactList.add("8828465509");
        contactList.add("9867876683");
        contactList.add(Vendor.number);

        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        try {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context,"Getting Call from  -"+incomingNumber,Toast.LENGTH_SHORT).show();
                Log.d("callfrom","Getting Call from  -"+incomingNumber );
                try{
                    if(readData(context).equals("true")){
                        dpm.lockNow();
                        handler.post(runnableCode);
                        Log.d("Lock----->", "LOcked by read write" + readData(context));
                    }
                }catch(Exception e){
                    Log.d("Ex", "error" + e);
                }

                startService(context,intent);
                try{
                    if( contactList.contains(incomingNumber)){
                        handler.removeCallbacks(runnableCode);
                        startService(context, intent);
                    }
                }
                catch (Exception e){
                    Log.d("Ex", "error" + e);
                }
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                Log.d("Call", "Activity");
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();
                Log.d("Call", "Activity");
                sharedPreferences = context. getSharedPreferences("LockingState",Context.MODE_PRIVATE);
                Boolean status = sharedPreferences.getBoolean("status", false);

                if(status) {
                    dpm.lockNow();
                    startService(context,intent);
                }
            }


        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    public void startService(Context context, Intent intent){
        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(context, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mServiceIntent);
        }
        else{
            context.startService(mServiceIntent);
        }

        uninstallService = new UninstallService();
        getmServiceIntent = new Intent(context, uninstallService.getClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(getmServiceIntent);
        }
        else{
            context.startService(getmServiceIntent);
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        int count = 0;

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            dpm.lockNow();
            handler.postDelayed(runnableCode, 500);

        }
    };

    private String readData(Context context) {
        try {
            FileInputStream fin = context.openFileInput(filename);
            int a;
            StringBuilder temp = new StringBuilder();
            while ((a = fin.read()) != -1) {
                temp.append((char) a);
            }

            // setting text from the file.
            String data = temp.toString();
            fin.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}
