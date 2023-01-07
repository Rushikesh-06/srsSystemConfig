package com.emi.systemconfiguration;

import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class EmiDueDate extends AppCompatActivity {

//    GridView coursesGV;
    public DevicePolicyManager mDPM;
    public ComponentName mDeviceAdmin;
    SharedPreferences preferences;
    TextView imei,finance,emi_amount,display_name,contact,payment;

    ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.emi.action.unlock")){
                setDefaultCosuPolicies(false);
                Intent intent1 = new Intent(EmiDueDate.this,MainActivity.class);
                intent1.putExtra("lastpage",getClass().getSimpleName());
                startActivity(intent1);
                finish();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        preferences = getSharedPreferences("EMILOCKER", MODE_PRIVATE);
//  Contact your  shop
//        9892580308
//        NA
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.emi.action.unlock");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        win.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        db = FirebaseFirestore.getInstance();
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // Set DeviceAdmin Demo Receiver for active the component with different option
        mDeviceAdmin = new ComponentName(this, DeviceAdmin.class);

        setContentView(R.layout.activity_emi_due_date);
//        LinearLayout background = (LinearLayout) findViewById(R.id.linearLayout);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        imei = findViewById(R.id.imei);
        finance = findViewById(R.id.finance);
        emi_amount = findViewById(R.id.emi_amount);
        display_name = findViewById(R.id.display_name);
        contact = findViewById(R.id.contact);
        payment = findViewById(R.id.payment);
        imei.setText(telephonyManager.getImei());
        finance.setText(preferences.getString("financiarName","NA"));
        emi_amount.setText(preferences.getString("emiAmount","NA"));
        display_name.setText(preferences.getString("displayName","Contact your shop"));
        contact.setText(preferences.getString("displayContactNumber","9892580308"));
        payment.setText(preferences.getString("paymentNumber","NA"));

        ActionBar actionBar = getSupportActionBar();
        // or getActionBar();
        getSupportActionBar().setLogo(R.drawable.goelctronixc);
        getSupportActionBar().setTitle("Anti-Theft Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar
        setDefaultCosuPolicies(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:

            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_VOLUME_UP:

            case KeyEvent.KEYCODE_BACK:

            case KeyEvent.KEYCODE_POWER:

            case KeyEvent.KEYCODE_MOVE_HOME:
                return true;

            case KeyEvent.KEYCODE_HOME:
                Log.d("HomeClick", "Working");
                // context = this;
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void setDefaultCosuPolicies(boolean active) {
//        db.collection("users_status").document(getDeviceId(getApplicationContext())).update("lockStatus", active);
        if (active) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (mDPM.isDeviceOwnerApp(this.getPackageName())) {
                    // Device owner
                    String[] packages = {this.getPackageName()};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mDPM.setLockTaskPackages(mDeviceAdmin, packages);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            mDPM.setLockTaskFeatures(mDeviceAdmin, DevicePolicyManager.LOCK_TASK_FEATURE_NONE);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mDPM.isLockTaskPermitted(this.getPackageName())) {
                            // Lock allowed
                            startLockTask();
                        } else {
                            // Lock not allowed - show error or something useful here
                            Toast.makeText(this, "Not lock found", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    // Not a device owner - prompt user or show error
                    Toast.makeText(this, "Not admin found", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (mDPM.isDeviceOwnerApp(this.getPackageName())) {
                    // Device owner
                    String[] packages = {this.getPackageName()};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mDPM.setLockTaskPackages(mDeviceAdmin, packages);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mDPM.isLockTaskPermitted(this.getPackageName())) {
                            // Lock allowed
                            stopLockTask();
                        } else {
                            // Lock not allowed - show error or something useful here
                            Toast.makeText(this, "Not lock found", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    // Not a device owner - prompt user or show error
                    Toast.makeText(this, "Not admin found", Toast.LENGTH_LONG).show();
                }
            }

        }

        // Set user restrictions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
            setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
            setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
            setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);
//             setUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, active);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE, active);
            }
            setUserRestriction(UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS, active);
            setUserRestriction(UserManager.DISALLOW_CREATE_WINDOWS, active);
            setUserRestriction(UserManager.DISALLOW_CONFIG_WIFI, active);
             setUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES,active);
            setUserRestriction(UserManager.DISALLOW_NETWORK_RESET, active);
            setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
            mDPM.setKeyguardDisabled(mDeviceAdmin, active);
            mDPM.setStatusBarDisabled(mDeviceAdmin, active);

        }

        // Enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // Set system update policy
        if (active) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDPM.setSystemUpdatePolicy(mDeviceAdmin, SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDPM.setSystemUpdatePolicy(mDeviceAdmin, null);
            }
        }

        // set this Activity as a lock task package
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDPM.setLockTaskPackages(mDeviceAdmin, active ? new String[]{getPackageName()} : new String[]{});
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.addPersistentPreferredActivity(mDeviceAdmin, intentFilter,
                        new ComponentName(getPackageName(), EmiDueDate.class.getName()));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.clearPackagePersistentPreferredActivities(mDeviceAdmin, getPackageName());
            }
        }
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.addUserRestriction(mDeviceAdmin, restriction);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.clearUserRestriction(mDeviceAdmin, restriction);
            }
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled) {
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.setGlobalSetting(mDeviceAdmin, Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                        Integer.toString(BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB
                                | BatteryManager.BATTERY_PLUGGED_WIRELESS));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.setGlobalSetting(mDeviceAdmin, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, "0");
            }
        }
    }


}
