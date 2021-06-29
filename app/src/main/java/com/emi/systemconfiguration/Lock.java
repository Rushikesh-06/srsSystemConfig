package com.emi.systemconfiguration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
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

//    String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        passcodeView = findViewById(R.id.passcodeView);

        db = FirebaseFirestore.getInstance();

        checkPassword();

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


    }

    private void setPassword(String pin){

        passcodeView.setPasscodeLength(5).setLocalPasscode(pin).setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(Lock.this, " Password is Wrong", Toast.LENGTH_SHORT).show();
                homeScreenIntent();
            }

            @Override
            public void onSuccess(String number) {
                Toast.makeText(Lock.this, "Code is right", Toast.LENGTH_SHORT).show();
                countDown();
//                finish();
//                count();
            }
        });
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
                        setPassword("69691");
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
            setPassword("69691");
        }
    }

    private  void countDown(){
        Boolean  isconnected=MainActivity.isConnected(getApplicationContext());

        if (isconnected) {

            String deviceId = MainActivity.getDeviceId(this);
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

            db.collection("users").document(deviceId).update("customer_active", false);
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long hour = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    Log.d("Count DOWN", f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                }

                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    Log.d("Count DOWN", "finished");
                    Random r = new Random();
                    int randomNumber =10000 + r.nextInt(90000);
                    Log.d("Random number", String.valueOf(randomNumber ));
                    db.collection("users").document(deviceId).update("customer_active", true,"customer_pincode",String.valueOf(randomNumber));
                    startBgActivity();

                }
            }.start();
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
