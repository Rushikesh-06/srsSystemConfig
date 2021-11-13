package com.emi.systemconfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.accounts.AccountManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;

import android.app.admin.FactoryResetProtectionPolicy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import android.content.pm.PackageInstaller;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Environment;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;
import static android.os.UserManager.DISALLOW_BLUETOOTH;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static android.os.UserManager.DISALLOW_MODIFY_ACCOUNTS;
import static android.os.UserManager.DISALLOW_REMOVE_MANAGED_PROFILE;
import static android.os.UserManager.DISALLOW_REMOVE_USER;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static android.service.controls.ControlsProviderService.TAG;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_3 = 3;
    public ComponentName mDeviceAdmin;
    public DevicePolicyManager mDPM;
    public TextView mToggleAdminBtn;
    public static final int REQUEST_CODE = 0, REQUEST_CODE_2 = 2 ;

    Boolean AllPerm = true;


    Button checkEmailBtn;
    TextView permissionText;

    private FirebaseFirestore db;

    public static Boolean multiFound = true;

    //For Permission
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int  ACCESS_NETWORK_STATE_CODE= 102;
    private static final int PACKAGE_USAGE_STATS_CODE= 103;
    private static final int ACCESS_FINE_LOCATION_CODE= 104;
    private static final int READ_PHONE_STATE_CODE= 105;



    public static final int MAKE_USER_EPHEMERAL = 1;

    private DownloadManager mDownloadManager;


    String IMEINumber;
    String manufacturer = android.os.Build.MANUFACTURER;

    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService LocationService;
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
            Manifest.permission.READ_PHONE_STATE
    };

    @SuppressLint({"WrongViewCast", "WrongThread"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        createNotficationchannel();
        //Firebase Istance
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailText =(EditText) findViewById(R.id.emailId);
        passwordText =(EditText) findViewById(R.id.editTextPassword);


        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Emi-Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.system_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar

        pass =password.getInstance();

        permissionText = findViewById(R.id.permissionText);

        //        Hide the textview and edittext
        registerText =(TextView) findViewById(R.id.registerText);
        registerText.setEnabled(true);

        checkEmailBtn = findViewById(R.id.emailBtn);
        checkEmailBtn.setEnabled(false);
        checkEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
//                    checkEmail();
                    loginUserAccount();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        requestPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isAccessGranted()) {
                getUsagePermission();
            }
            if(!Settings.canDrawOverlays(this)){
                getdrawPermission();
            }
        }


        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdmin Demo Receiver for active the component with different option
            mDeviceAdmin = new ComponentName(this, DeviceAdmin.class);


            if(!hasPermissions(this, PERMISSIONS)){
//                String[] permissions = this.getPackageManager().getPackageInfo(this.getPackageName(),PackageManager.GET_PERMISSIONS).requestedPermissions;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()){
                        Log.d("Tag", "OWrking");
                    }else{
                        Intent intent = new Intent();
                        intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        loginText.setEnabled(false);
                    }
                }

                for (String permission : PERMISSIONS) {
                    boolean success = mDPM.setPermissionGrantState(mDeviceAdmin, this.getPackageName(), permission, PERMISSION_GRANT_STATE_GRANTED);

                    if (!success) {
                        Log.e(TAG, "Failed to auto grant permission to self: " + permission);
                    }
                }
            }

            checkEmailBtn.setEnabled(true);
            registerText.setEnabled(true);
            permissionText.setVisibility(View.GONE);
//            Intent fromIntent = getIntent();
//            String flag = fromIntent.hasExtra("flag") ? fromIntent.getStringExtra("flag") : "";

            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_FACTORY_RESET);
//            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_BLUETOOTH);
            mDPM.addUserRestriction(mDeviceAdmin, UserManager.DISALLOW_USB_FILE_TRANSFER);
//            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_MODIFY_ACCOUNTS);

            if (!mDPM.isAdminActive(mDeviceAdmin)) {
                // try to become active
                Log.d("note", "request for admin");
                getDeviceAdminPermsion();
            }

            Bundle bundle = new Bundle();
            String recoveryAccount[] = {
                    "101251806639257169134", //elocker568-ID
                    "104806275544500277760", //elitnotch-ID
            };

            bundle.putStringArray("factoryResetProtectionAdmin", recoveryAccount);
            mDPM.setApplicationRestrictions(mDeviceAdmin, "com.google.android.gms", bundle);

            Intent broadcastIntent =new Intent("com.google.android.gms.auth.FRP_CONFIG_CHANGED");
            broadcastIntent.setPackage("com.google.android.gms");
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            sendBroadcast(broadcastIntent);

        }
        catch(Exception e) {
            Log.d("Error", e.toString());
            e.printStackTrace();
        }



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
                askPassword();
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
                    Intent myService = new Intent(getApplicationContext(), BackgroundService.class);
                    stopService(myService);
                    db.collection("users").document(deviceId).update("customer_pincode", Integer.toString(randomNumber));
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
            DocumentReference documentReference = db.collection("users").document(deviceId);
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


    public void getUsagePermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_3);
    }

    public void getdrawPermission() {

        Intent draw = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(draw, REQUEST_CODE_2);
    }

    public void getDeviceAdminPermsion() {


        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
        startActivityForResult(intent, REQUEST_CODE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_FACTORY_RESET);
//            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_BLUETOOTH);
//            mDPM.addUserRestriction(mDeviceAdmin, DISALLOW_MODIFY_ACCOUNTS);
            mDPM.addUserRestriction(mDeviceAdmin, UserManager.DISALLOW_USB_FILE_TRANSFER);
        }

    }

    public void checkEmail() {
        auth.fetchProvidersForEmail(emailText.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                boolean check = !task.getResult().getProviders().isEmpty();

                if (!check) {
                    Intent registrationIntent = new Intent(getApplicationContext(), RegistrationAcitivity.class);
                    startActivity(registrationIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Email Found", Toast.LENGTH_LONG).show();
                    backgroundService = new BackgroundService();
                    mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
                    if (!isMyServiceRunning(backgroundService.getClass())) {
                        startService(mServiceIntent);
                    }
                }
            }
        });
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
                                } else {

                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(),
                                            "Login failed!!",
                                            Toast.LENGTH_LONG)
                                            .show();

                                    // hide the progress bar

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
      }
      catch(Exception e){
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


    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
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

                                checkEmailBtn.setEnabled(true);
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


    public void startAllServices() {

//        pass.setLockState(false);
//        startLockTimerInit(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        backgroundService = new BackgroundService();
        mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
        if (!isMyServiceRunning(backgroundService.getClass())) {
            startService(mServiceIntent);
        }

        uninstallService = new UninstallService();
        mServiceIntent = new Intent(getApplicationContext(), uninstallService.getClass());
        if (!isMyServiceRunning(uninstallService.getClass())) {
            startService(mServiceIntent);
        }

        backgroundDelayService = new BackgroundDelayService();
        mServiceIntent = new Intent(getApplicationContext(), backgroundDelayService.getClass());
        if (!isMyServiceRunning(backgroundDelayService.getClass())) {
            startService(mServiceIntent);
        }

        LocationService = new LocationService();
        mServiceIntent = new Intent(getApplicationContext(), LocationService.getClass());
        if (!isMyServiceRunning(LocationService.getClass())) {
            startService(mServiceIntent);
        }

        Toast.makeText(this, "All service started successfully don't need to login", Toast.LENGTH_SHORT).show();

    }

    public void registerActivity(View view) {
        if (AllPerm) {
            Intent registrationIntent = new Intent(getApplicationContext(), RegistrationAcitivity.class);
            startActivity(registrationIntent);
        } else {
            Toast.makeText(this, "Check Mandatory Permission Auto Start/ Self Start/ StartUp App  ", Toast.LENGTH_LONG).show();

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

    public void welcome(View v) {

        Intent registrationIntent = new Intent(getApplicationContext(), Welcome.class);
        startActivity(registrationIntent);
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



    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createUser(View v) {

//        installApk();
//        uninstallApk();
//        createGoogleAccout();

    }

    public void createGoogleAccount(View v) {
        AccountManager acm = AccountManager.get(getApplicationContext());
        acm.addAccount("com.google", null, null, null, MainActivity.this,
                null, null);
    }

    private  void DownloadApk(){
        Toast.makeText(this, "Started to Download", Toast.LENGTH_SHORT).show();
        String url = "https://goelectronix.s3.us-east-2.amazonaws.com/AntiTheftV1.apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download EmiLocker");
        request.setDescription("Downloading EmiLocker");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"AntiTheftV1.apk");
        DownloadManager manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void installApk() {
//
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PackageInstaller pi = this.getPackageManager().getPackageInstaller();
                int sessId = pi.createSession(new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));

                PackageInstaller.Session session = pi.openSession(sessId);

                // .. write updated APK file to out
                long sizeBytes = 0;
                final File file = new File("/storage/emulated/0/Download/AntiTheftV1.apk");
                if (file.isFile()) {
                    sizeBytes = file.length();
                }

                InputStream in = null;
                OutputStream out = null;

                in = new FileInputStream("/storage/emulated/0/Download/AntiTheftV1.apk");
                out = session.openWrite("my_app_session", 0, sizeBytes);

                int total = 0;
                byte[] buffer = new byte[65536];
                int c;
                while ((c = in.read(buffer)) != -1) {
                    total += c;
                    out.write(buffer, 0, c);
                }

                    session.fsync(out);

                in.close();
                out.close();

                System.out.println("InstallApkViaPackageInstaller - Success: streamed apk " + total + " bytes");
                // fake intent
                Context app = this;
                Intent intent = new Intent(app, DeviceAdmin.class);
                PendingIntent alarmtest = PendingIntent.getBroadcast(app,
                        1337111117, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                session.commit(alarmtest.getIntentSender());

                session.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void uninstallApk() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.clearDeviceOwnerApp(this.getPackageName());
//        String appPackage = "com.emi.systemconfiguration";
//        Intent intent = new Intent(this, this.getClass());
//        PendingIntent sender = PendingIntent.getActivity(this, 0, intent, 0);
//        PackageInstaller mPackageInstaller = this.getPackageManager().getPackageInstaller();
//        mPackageInstaller.uninstall(appPackage, sender.getIntentSender());

    }

}


