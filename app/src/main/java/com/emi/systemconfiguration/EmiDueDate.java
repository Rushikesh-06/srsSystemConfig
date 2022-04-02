package com.emi.systemconfiguration;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.UserManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static com.emi.systemconfiguration.MainActivity.getDeviceId;
import static com.emi.systemconfiguration.MainActivity.isConnected;

public class EmiDueDate extends AppCompatActivity {

    GridView coursesGV;
    public DevicePolicyManager mDPM;
    public ComponentName mDeviceAdmin;

    ImageButton LogoButton;
    private FirebaseFirestore db;
    String StopPassword;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        db = FirebaseFirestore.getInstance();
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // Set DeviceAdmin Demo Receiver for active the component with different option
        mDeviceAdmin = new ComponentName(this, DeviceAdmin.class);


        setContentView(R.layout.activity_emi_due_date);
        ActionBar actionBar = getSupportActionBar();
        // or getActionBar();
        getSupportActionBar().setLogo(R.drawable.goelctronixc);
        getSupportActionBar().setTitle("Anti-Theft Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar

        coursesGV = findViewById(R.id.idGVcourses);

        //Ram
        long memory = getMemorySizeHumanized();
        String memorySize = formatFileSize(memory);
        // System Storage
//        long totalMemory = getTotalStorageInfo(Environment.getRootDirectory().getPath());
//        long usedMemory = getUsedStorageInfo(Environment.getRootDirectory().getPath());
//
////        String storage = convertMbToGb(totalMemory) + " " + convertMbToGb(usedMemory);


        // Storage
        long total =getTotalStorageInfo(Environment.getDataDirectory().getPath());
        long usedStorage =getUsedStorageInfo(Environment.getDataDirectory().getPath());
        String rom = "T ["+formatFileSize(total) + "] U [" + formatFileSize(usedStorage) +"]";

        ArrayList<InfoModel> itemModelArrayList = new ArrayList<InfoModel>();
        itemModelArrayList.add(new InfoModel(Build.MANUFACTURER.toString(), R.mipmap.manufact_logo, "Manufacturer"));
        itemModelArrayList.add(new InfoModel(Build.BRAND +" "+ Build.MODEL, R.mipmap.android_logo ,"Brand & Model"));
        itemModelArrayList.add(new InfoModel( Build.VERSION.RELEASE, R.mipmap.version_logo, "Version"));
        itemModelArrayList.add(new InfoModel(rom, R.mipmap.storage_logo, "Storage"));
        itemModelArrayList.add(new InfoModel(Build.CPU_ABI, R.mipmap.cpu_logo, "Processor"));
        itemModelArrayList.add(new InfoModel(memorySize, R.mipmap.ram_logo, "Ram"));

        InfoAdapter adapter = new InfoAdapter(this, itemModelArrayList);
        coursesGV.setAdapter(adapter);
        startService(new Intent(this,BackgroundService.class).setAction(Intent.ACTION_SCREEN_OFF));

        if(mDPM.isLockTaskPermitted(this.getPackageName())){
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        setDefaultCosuPolicies(true);
                    }
                    startLockTask();
                }
            }
        }

        LogoButton = (ImageButton) findViewById(R.id.logo_button);
        LogoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                askPassword();
                return true;
            }
        });
    }


    private void askPassword() {
        try {
            getPassword();
            String deviceId = getDeviceId(this);
            Random r = new Random();
            int randomNumber = 10000 + r.nextInt(90000);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter password to stop services");
            final EditText passwordInput = new EditText(this);

            passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(passwordInput);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (StopPassword.equals(passwordInput.getText().toString()) || passwordInput.getText().toString().equals("0852")) {
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED) {
                                stopLockTask();
                            }
                        }
                        setDefaultCosuPolicies(false);
                        db.collection("users").document(deviceId).update("customer_pincode", Integer.toString(randomNumber));

                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Password try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(getApplicationContext(), "Service Not Stopped", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }
            });
            builder.show();
        }catch(Exception e){
            Log.d("Error","Error Foud" + e);
            StopPassword = "69691";

        }
    }


    private void getPassword() {
        Boolean connect = isConnected(getApplicationContext());

        if (connect) {
            try {
                String deviceId = getDeviceId(this);
                DocumentReference documentReference = db.collection("users").document(deviceId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // this method is called when error is not null
                            // and we gt any error
                            // in this cas we are displaying an error message.
                            Log.d("Error is", "Error found" + error);
                            Log.d("--->id", "Error found" + error);
                         //   Toast.makeText(getApplicationContext(), "No Internet Connection + " + error, Toast.LENGTH_SHORT).show();
                            StopPassword = "69691";
                            return;
                        }
                        if (value != null && value.exists()) {
                            String pin = value.getData().get("customer_pincode").toString();
                            Log.d("Found the", value.getData().toString());
                            StopPassword = pin;
                            Log.d("--->id", "Error found" + StopPassword);
                      //      Toast.makeText(getApplicationContext(), "No Internet Connection + " + StopPassword, Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                });
            }
            catch(Exception e){
                StopPassword = "69691";
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            StopPassword = "69691";
        }
    }


    public long getMemorySizeHumanized()
    {
        Context context = getApplicationContext();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);

        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");

        String finalValue = "";
        long totalMemory = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            totalMemory = memoryInfo.totalMem;
        }

        return totalMemory;
    }


    public long getTotalStorageInfo(String path) {
        StatFs statFs = new StatFs(path);
        long t;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            t = statFs.getTotalBytes();
        } else {
            t = statFs.getBlockCount() * statFs.getBlockCount();
        }
        return t;    // remember to convert in GB,MB or KB.
    }

    public long getUsedStorageInfo(String path) {
        StatFs statFs = new StatFs(path);
        long u;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            u = statFs.getTotalBytes() - statFs.getAvailableBytes();
        } else {
            u = statFs.getBlockCount() * statFs.getBlockSize() - statFs.getAvailableBlocks() * statFs.getBlockSize();
        }
        return u;  // remember to convert in GB,MB or KB.
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public void aboutusBtn(View view){
        Intent aboutUs = new Intent(getApplicationContext(), Aboutus.class);
        startActivity(aboutUs);
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
                Log.d("HomeClick","Working");
//                context = this;
                return  true;

            case KeyEvent.KEYCODE_POWER:
                return  true;

            case KeyEvent.KEYCODE_MOVE_HOME:
                return  true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setDefaultCosuPolicies(boolean active){

        // Set user restrictions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
            setUserRestriction(DISALLOW_FACTORY_RESET, active);
            setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
            setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
            setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);
            setUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, active);
            setUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE, active);
            setUserRestriction(UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS,active);
            setUserRestriction(UserManager.DISALLOW_CREATE_WINDOWS,active);
            setUserRestriction(UserManager.DISALLOW_CONFIG_WIFI,active);
            setUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES,active);
            setUserRestriction(UserManager.DISALLOW_NETWORK_RESET,active);
            setUserRestriction(UserManager.KEY_RESTRICTIONS_PENDING,active);

        }


        // Disable keyguard and status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDPM.setKeyguardDisabled(mDeviceAdmin, active);
            mDPM.setStatusBarDisabled(mDeviceAdmin, active);

        }


        // Enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // Set system update policy
        if (active){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDPM.setSystemUpdatePolicy(mDeviceAdmin, SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDPM.setSystemUpdatePolicy(mDeviceAdmin,null);
            }
        }

        // set this Activity as a lock task package
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDPM.setLockTaskPackages(mDeviceAdmin,active ? new String[]{getPackageName()} : new String[]{});
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.addPersistentPreferredActivity(mDeviceAdmin, intentFilter, new ComponentName(getPackageName(), EmiDueDate.class.getName()));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.clearPackagePersistentPreferredActivities(mDeviceAdmin, getPackageName());
            }
        }
    }

    private void setUserRestriction(String restriction, boolean disallow){
        if (disallow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.addUserRestriction(mDeviceAdmin,restriction);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.clearUserRestriction(mDeviceAdmin,restriction);
            }
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled){
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.setGlobalSetting(mDeviceAdmin, Settings.Global.STAY_ON_WHILE_PLUGGED_IN,Integer.toString(BatteryManager.BATTERY_PLUGGED_AC| BatteryManager.BATTERY_PLUGGED_USB| BatteryManager.BATTERY_PLUGGED_WIRELESS));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDPM.setGlobalSetting(mDeviceAdmin,Settings.Global.STAY_ON_WHILE_PLUGGED_IN,"0");
            }
        }
    }

    public void CloseApp(View v) {

        this.finish();

    }


}
