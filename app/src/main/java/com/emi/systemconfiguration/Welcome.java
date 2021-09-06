package com.emi.systemconfiguration;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Welcome extends AppCompatActivity {
    public final static int REQUEST_CODE = 10101;
    private PowerButtonService powerButtonService;
    Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
//        startService(new Intent(getApplicationContext(), PowerButtonService.class));
//        if (checkDrawOverlayPermission()) {
//            startService(new Intent(this, PowerButtonService.class));
//        }
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE) {
//            if (Settings.canDrawOverlays(this)) {
//                startService(new Intent(this, PowerButtonService.class));
//            }
//        }
//    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        hideNavigationBar();
//    }
//    private void hideNavigationBar() {
//        final View decorView = this.getWindow().getDecorView();
//        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                Welcome.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        decorView.setSystemUiVisibility(uiOptions);
//                    }
//                });
//            }
//        };
//        timer.scheduleAtFixedRate(task, 1, 2);
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if ( (event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
            return true;
        }
        else if(event.getKeyCode()==KeyEvent.KEYCODE_POWER){
            Log.d("###","Power button long click");
            Toast.makeText(Welcome.this, "Clicked: bdfbfd", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Boolean result;
        switch( event.getKeyCode() ) {

            case KeyEvent.KEYCODE_MENU:
                result = true;
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                result = true;
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                result = true;
                break;
            case KeyEvent.KEYCODE_BACK:
                result = true;
                break;
            case KeyEvent.KEYCODE_POWER:
                Log.d("###","Power button long click");
//                Toast.makeText(Welcome.this, "Clicked: bdfbfd", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                result = true;
                break;
            case KeyEvent.KEYCODE_HOME:
                Intent dialogIntent = new Intent(this, Welcome.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
                result = true;
                break;
            default:
                result= super.dispatchKeyEvent(event);
                break;
        }

        return result;
    }

    @Override
    public boolean onKeyLongPress( int keyCode, KeyEvent event ) {
        if( keyCode == KeyEvent.KEYCODE_POWER ) {
            //Handle what you want in long press.
            Log.d("###","Power button long click");
//            Toast.makeText(Welcome.this, "Clicked: bdfbfd", Toast.LENGTH_SHORT).show();
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            return true;
        }
        return super.onKeyLongPress( keyCode, event );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (! hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            GlobalLock globalLock = com.emi.systemconfiguration.GlobalLock.getInstance();
            globalLock.setData(true);

            powerButtonService = new PowerButtonService();

            mServiceIntent = new Intent(getApplicationContext(), PowerButtonService.class);
            startService(mServiceIntent);

        }
    }



}
