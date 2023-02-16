package com.emi.systemconfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;

import android.app.admin.SystemUpdatePolicy;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Environment;
import android.os.PowerManager;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;

import static android.service.controls.ControlsProviderService.TAG;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_3 = 3;
    private static final int REGISTER_REQUEST = 122;
    public ComponentName mDeviceAdmin;
    public DevicePolicyManager mDPM;
    public TextView mToggleAdminBtn;
    public static final int REQUEST_CODE = 0, REQUEST_CODE_2 = 2;

    Boolean AllPerm = true;

    //    Button checkEmailBtn;
    TextView permissionText;

//    private FirebaseFirestore db;

    public static Boolean multiFound = true;
    String generatedString;

    //For Permission
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int ACCESS_NETWORK_STATE_CODE = 102;
    private static final int PACKAGE_USAGE_STATS_CODE = 103;
    private static final int ACCESS_FINE_LOCATION_CODE = 104;
    private static final int READ_PHONE_STATE_CODE = 105;


    public static final int MAKE_USER_EPHEMERAL = 1;

    private DownloadManager mDownloadManager;


    String IMEINumber;
    String manufacturer = android.os.Build.MANUFACTURER;

    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private UpdateService updateService;
    private UninstallService uninstallService;

    Intent mServiceIntent;
    FirebaseAuth auth;

    UserManager userManager;

    EditText emailText;
    EditText passwordText;
    TextView registerText, loginText;
    password pass;


    String MultiUser;
    String StopPassword;

    String PathURL;


    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_SECURE_SETTINGS,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.INSTALL_PACKAGES,
            Manifest.permission.BATTERY_STATS,
    };
    SharedPreferences sharedPreferences;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Context context;
    String token;
//    APp install device admin


    public static final int MSG_DOWNLOAD_COMPLETE = 1;
    public static final int MSG_DOWNLOAD_TIMEOUT = 2;
    public static final int MSG_INSTALL_COMPLETE = 3;

    private static final int DOWNLOAD_TIMEOUT_MILLIS = 120_000;

    public static final String ACTION_INSTALL_COMPLETE
            = "com.emi.systemconfiguration.INSTALL_COMPLETE";


    String syncAPI = "http://api.srsemiprotect.com/api/app/CustomerStatusSync";

    //display window if already register
    FrameLayout mainFramelayout;
    LinearLayout registerscreenlayout;
    SessionManage session;
    Fragment fragment;
    FragmentTransaction transaction;

    @SuppressLint({"WrongViewCast", "WrongThread"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //firebase push notification

      /*  if (getIntent().hasExtra("lastpage")) {
            if (getIntent().getStringExtra("lastpage").equalsIgnoreCase("EmiDueDate")) {
                finish();
                return;
            }
        }*/


        //display window if already register
        registerscreenlayout = findViewById(R.id.registerscreenlayout);
        mainFramelayout = findViewById(R.id.mainFramelayout);
        fragment = new RegisteredCustDetail_Fragment();
        transaction = getSupportFragmentManager().beginTransaction();
        session = new SessionManage(MainActivity.this);

        if (session.getregisteredStatus()) {
            transaction.replace(R.id.mainFramelayout, fragment);
            transaction.commit();
        } else {
            transaction.remove(fragment);
            transaction.commit();
        }


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        Intent batterystatus = context.registerReceiver(new BroadcastReciever(),intentFilter);


        SharedPreferences sharedPreferences = getSharedPreferences("LockingState", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("status", false)) {
            Intent dialogIntent = new Intent(this, EmiDueDate.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
        }
//        db = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("EMILOCKER", MODE_PRIVATE);
        editor = preferences.edit();
        permissionText = findViewById(R.id.permissionText);
        sharedPreferences = getSharedPreferences("LockingState", MODE_PRIVATE);
         permissionText.setVisibility(View.VISIBLE);
        permissionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", token);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "copied", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        System.out.println(token);
                        Toast.makeText(MainActivity.this, "You device registration token is :  " + token,
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onComplete: " + token);

                        editor.putString("fcm_token", token);
                        editor.commit();
                    }
                });
        FirebaseMessaging.getInstance().subscribeToTopic("customer")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

//        createNotficationchannel();
        //Firebase Istance
        auth = FirebaseAuth.getInstance();

       /* sharedPreferences = getSharedPreferences("LockingState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", false);
        editor.apply();*/


        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Emi-Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.system_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar

        pass = password.getInstance();


        //        Hide the textview and edittext
        registerText = (TextView) findViewById(R.id.registerText);
        registerText.setEnabled(true);

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdmin Demo Receiver for active the component with different option
            mDeviceAdmin = new ComponentName(this, DeviceAdmin.class);


            if (!hasPermissions(this, PERMISSIONS)) {

                for (String permission : PERMISSIONS) {
                    boolean success = mDPM.setPermissionGrantState(mDeviceAdmin, this.getPackageName(), permission, PERMISSION_GRANT_STATE_GRANTED);

                    if (!success) {
                        Log.e(TAG, "Failed to auto grant permission to self: " + permission);
                    }
                }
            }

            registerText.setEnabled(true);


            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_FACTORY_RESET);
            mDPM.addUserRestriction(mDeviceAdmin, UserManager.DISALLOW_USB_FILE_TRANSFER);
            mDPM.addUserRestriction(mDeviceAdmin,UserManager.DISALLOW_SAFE_BOOT);

            mDPM.setUninstallBlocked(mDeviceAdmin, getPackageName(), true);
            if (!mDPM.isAdminActive(mDeviceAdmin)) {
                // try to become active
                Log.d("note", "request for admin");
                getDeviceAdminPermsion();

            }

            Bundle bundle = new Bundle();
            String recoveryAccount[] = {
                    "101251806639257169134", //elocker568-ID
            };

            bundle.putStringArray("factoryResetProtectionAdmin", recoveryAccount);
            mDPM.setApplicationRestrictions(mDeviceAdmin, "com.google.android.gms", bundle);

            Intent broadcastIntent = new Intent("com.google.android.gms.auth.FRP_CONFIG_CHANGED");
            broadcastIntent.setPackage("com.google.android.gms");
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            sendBroadcast(broadcastIntent);

            batteryOptimize();
//            startAllServices();

        } catch (Exception e) {
            Log.d("Error", e.toString());
            e.printStackTrace();
        }
//        requestPermissions();


        Boolean isconnected = MainActivity.isConnected(getApplicationContext());
        if (isconnected) {
            if (auth.getCurrentUser() != null) {
                updateVendor();
                startAllServices();

            }
        }


//        Stop Service
        TextView anti = (TextView) findViewById(R.id.anti);
        anti.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                askPassword();
                return true;
            }
        });

        loginText = (TextView) findViewById(R.id.textView8);
        loginText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addAutoStartup();
                return true;
            }
        });

        CallsyncAPI();
/*
        try {
            Runtime.getRuntime().exec("dpm set-device-admin --user 0 com.emi.systemconfiguration/com.emi.systemconfiguration.DeviceAdmin");
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

    }

    private void CallsyncAPI() {

        JSONObject params = new JSONObject();

        String deviceid = MainActivity.getDeviceId(getApplicationContext());
        String newFCMtoken = preferences.getString("fcm_token", "NA");
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            params.put("DeviceID", deviceid);
            if (!BuildConfig.DEBUG) {
                params.put("IMEINumber", telephonyManager.getImei());
            }
            params.put("FirebaseToken", newFCMtoken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, syncAPI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                Log.e("Call Sync API-response :", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Call Sync API-error :", error.toString());
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(objectRequest);
    }

    private void askPassword() {
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
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (StopPassword.equals(passwordInput.getText().toString()) || passwordInput.getText().toString().equals("0852")) {
                    Toast.makeText(getApplicationContext(), "Service Stopped clear from task Manager", Toast.LENGTH_LONG).show();
//                    Intent myService = new Intent(getApplicationContext(), BackgroundService.class);
//                    stopService(myService);
//                    db.collection("users").document(deviceId).update("customer_pincode", Integer.toString(randomNumber));
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Password try again", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "Service Not Stopped", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void getPassword() {
        Boolean connect = isConnected(getApplicationContext());

        if (connect) {
            String deviceId = getDeviceId(this);
//            DocumentReference documentReference = db.collection("users").document(deviceId);
/*
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // this method is called when error is not null
                        // and we gt any error
                        // in this cas we are displaying an error message.
                        Log.d("Error is", "Error found" + error);

                        StopPassword = "69691";
                        return;
                    }
                    if (value != null && value.exists()) {
                        String pin = value.getData().get("customer_pincode").toString();
                        Log.d("Found the", value.getData().toString());
                        StopPassword = pin;

                        return;

                    }
                }
            });
*/
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            StopPassword = "69691";
        }
    }


    private void startLockTimerInit(long seconds) {

        long maxCounter = seconds;
        long diff = 1000;
        new CountDownTimer(maxCounter, diff) {

            public void onTick(long millisUntilFinished) {
                long diff = maxCounter - millisUntilFinished;
                Log.d("Timer", "TimerTask" + diff / 1000);

                pass.setLockState(false);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                Log.d("Finish", "Task is finished");
                pass.setLockState(true);
            }

        }.start();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");

        //    Toast.makeText(this, "No runnng", Toast.LENGTH_LONG).show();
        return false;
    }

    public void getDeviceAdminPermsion() {


        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
        startActivityForResult(intent, REQUEST_CODE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_FACTORY_RESET);
//            mDPM.addUserRestriction(mDeviceAdmin, UserManager.DISALLOW_USB_FILE_TRANSFER);
        }

    }

    private void loginUserAccount() {
        // show the visibility of progress bar to show loading
        //  progressbar.setVisibility(View.VISIBLE);


        // Take the value of two edit texts in Strings
        String email, password;
        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // signin existing user

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(

                                    @NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                                    "Login successful!!",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    startAllServices();
                                    finish();


                                } else {

                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(),
                                                    "Login failed!!",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
    }

    //    Permission
    public void readPermission() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    Log.d(TAG, "Permission : " + p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVendor() {
        try {

            String deviceID = getDeviceId(this);

/*
            db.collection("policy").whereEqualTo("customerUid", deviceID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    if (task.getResult().getDocuments().size() > 0) {
                        String vendorId;
                        vendorId = task.getResult().getDocuments().get(0).get("vendorID").toString();


                        db.collection("vendors").document(vendorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                String vendorNumber = task.getResult().get("contact").toString();
                                Vendor.number = vendorNumber;
                                Log.d("Number", "---------->" + vendorNumber);

//                            Toast.makeText(getApplicationContext(),
//                                    vendorNumber,
//                                    Toast.LENGTH_LONG)
//                                    .show();
                            }
                        });

                    } else {

                        Log.d("Game", "Nt fund the vendor");
                    }

                }
            });
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to check and request permission

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }
        return deviceId;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == ACCESS_NETWORK_STATE_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Network Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "Network Permission Denied", Toast.LENGTH_SHORT).show();

            }

        } else if (requestCode == ACCESS_FINE_LOCATION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();

            }

        } else if (requestCode == READ_PHONE_STATE_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "All  Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Read Contact Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.RECEIVE_SMS


                            //         Manifest.permission.PACKAGE_USAGE_STATS
                            //                Manifest.permission.REQUEST_INSTALL_PACKAGES

                    )
                    // after adding permissions we are
                    // calling an with listener method.
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // this method is called when all permissions are granted
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // do you work now
                                Toast.makeText(MainActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();

                                registerText.setEnabled(true);
                                permissionText.setVisibility(View.GONE);

                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permanently,
                                // we will show user a dialog message.
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // this method is called when user grants some
                            // permission and denies some of them.
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                        // this method is use to handle error
                        // in runtime permissions
                        @Override
                        public void onError(DexterError error) {
                            // we are displaying a toast message for error message.
                            Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                    requestPermissions();
                        }
                    })
                    // below line is use to run the permissions
                    // on same thread and to check the permissions
                    .onSameThread().check();
        }
    }

    // below is the shoe setting dialog
    // method which is use to display a
    // dialogue message.
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel();
                // below is the intent from which we
                // are redirecting our user.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called when
                // user click on negative button.
                dialog.cancel();
            }
        });
        // below line is used
        // to display our dialog
        builder.show();
    }


    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @SuppressLint("BatteryLife")
    public void batteryOptimize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES));
                    }
                } catch (Exception e) {
                    Log.d("errr", e + "found");
                }
            }

        }
    }

    public void startAllServices() {


        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
        if (!isMyServiceRunning(backgroundService.getClass())) {
            startService(mServiceIntent);
        }
        updateService = new UpdateService();
        mServiceIntent = new Intent(getApplicationContext(), updateService.getClass());
        if (!isMyServiceRunning(updateService.getClass())) {
            startService(mServiceIntent);
        }

        uninstallService = new UninstallService();
        mServiceIntent = new Intent(getApplicationContext(), uninstallService.getClass());
        if (!isMyServiceRunning(uninstallService.getClass())) {
            startService(mServiceIntent);
        }


        Toast.makeText(this, "All service started successfully don't need to login", Toast.LENGTH_SHORT).show();
//        For hiding application
        hideApplication();
    }

    public void hideApplication() {
//        MEthod first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDPM.setApplicationHidden(mDeviceAdmin, "com.emi.systemconfiguration", true);
        }


//        MEthod two uncomment it if first wont work
//        PackageManager p = getPackageManager();
//        ComponentName componentName = new ComponentName(this, com.emi.systemconfiguration.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
//        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public void registerActivity(View view) {
//        if (AllPerm) {
        Intent registrationIntent = new Intent(getApplicationContext(), RegistrationAcitivity.class);
        startActivityForResult(registrationIntent, REGISTER_REQUEST);
//        } else {
//            Toast.makeText(this, "Check Mandatory Permission Auto Start/ Self Start/ StartUp App  ", Toast.LENGTH_LONG).show();
//
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REGISTER_REQUEST:
//                        transaction.replace(R.id.mainFramelayout, fragment);
//                        transaction.commit();
                        break;
                }
                break;

        }
    }

    public void forgetPassword(View view) {

        showRecoverPasswordDialog();
    }

    ProgressDialog loadingBar;

    @SuppressLint("ResourceAsColor")
    private void showRecoverPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailet = new EditText(this);


        // write the email using which you registered
//        emailet.setText("Email");
        emailet.setHint("Enter your Registered Email");
//        emailet.setBackgroundColor(R.drawable.linerbg);
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailet.setHintTextColor(getResources().getColor(R.color.colorHint));
        emailet.setTextColor(getResources().getColor(R.color.colorText));

        linearLayout.addView(emailet);

        linearLayout.setPadding(50, 20, 10, 20);

        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = emailet.getText().toString().trim();

                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(R.drawable.linerbg));
        builder.create().show();
        // Change the alert dialog background color

    }

    private void beginRecovery(String emaill) {

        loadingBar = new ProgressDialog(this);

        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        auth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();

                if (task.isSuccessful()) {
                    // if isSuccessful then done messgae will be shown
                    // and you can change the password
                    Toast.makeText(MainActivity.this, "Done sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();

                Toast.makeText(MainActivity.this, "Error Failed", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void addAutoStartup() {
        try {
            pass.setLockState(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Secret Code");
            final EditText passwordInput = new EditText(this);

            passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(passwordInput);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (passwordInput.getText().toString().equals("753951") || passwordInput.getText().toString().equals("951753") || passwordInput.getText().toString().equals("001122")) {

                        Toast.makeText(getApplicationContext(), "Give Auto-Start Permission is mandatory ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        AllPerm = true;
                        loginText.setEnabled(false);

                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Secret Code Pleas Try Again", Toast.LENGTH_LONG).show();

                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(getApplicationContext(), "Registration For you won't be active", Toast.LENGTH_LONG).show();

                    dialog.cancel();
                }
            });
            builder.show();


        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    @SuppressLint("WrongConstant")

    private void settingActivitiesInit() {

        try {
            PackageManager packageManager = getPackageManager();
            for (ActivityInfo activity : packageManager.getPackageInfo("com.android.settings", 1).activities) {
                if (activity.enabled && activity.exported) {

                    if (activity.loadLabel(packageManager).toString().contains("Multiple users") ||
                            activity.loadLabel(packageManager).toString().contains("Guest users")) {

                        Log.d("lable", activity.loadLabel(packageManager) + activity.name);
                        MultiUser = activity.name;
                    }
                }
            }

        } catch (Exception e) {

            multiFound = false;
            pass.setLockState(false);
            Toast.makeText(this, "Unable to find Multi User", Toast.LENGTH_SHORT).show();

            //     startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));

            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getKeyCode()) {


            case KeyEvent.KEYCODE_MENU:

            case KeyEvent.KEYCODE_MOVE_HOME:

                if (!multiFound) {
                    pass.setLockState(false);
                } else {

                    pass.setLockState(true);
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:

            case KeyEvent.KEYCODE_POWER:
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;

            case KeyEvent.KEYCODE_BACK:

                return true;

            case KeyEvent.KEYCODE_HOME:

                Log.d("HomeClick", "Working");
                if (!multiFound) {
                    pass.setLockState(false);
                } else {
                    pass.setLockState(true);
                }
//                context = this;
                return true;

            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onDestroy() {

        if (!multiFound) {
            pass.setLockState(false);
        } else {

            pass.setLockState(true);
        }
        super.onDestroy();
    }


    public void switchUser(View v) {
        pass.setLockState(false);
        startLockTimerInit(3000);

        try {


            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings", MultiUser));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } catch (Exception e) {

            Toast.makeText(this, "Unable to find Multi User", Toast.LENGTH_SHORT).show();
            multiFound = false;
            pass.setLockState(false);
            String manufacturer = android.os.Build.MANUFACTURER;
            e.printStackTrace();
        }
    }

    public void createGoogleAccount(View v) {
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
    }

    protected static String getSaltString(int lenght) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < lenght) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static String getSerialNumber() {
        String serialNumber = "";

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals("") || serialNumber.equals("unknown"))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals("") || serialNumber.equals("unknown"))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals("") || serialNumber.equals("unknown"))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals("") || serialNumber.equals("unknown"))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

}


