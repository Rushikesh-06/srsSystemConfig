package com.emi.systemconfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
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

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static android.os.UserManager.DISALLOW_OUTGOING_CALLS;
import static android.os.UserManager.DISALLOW_SMS;
import static android.service.controls.ControlsProviderService.TAG;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_3 = 3;
    public ComponentName mDeviceAdmin;
    public DevicePolicyManager mDPM;
    public TextView mToggleAdminBtn;
    public static final int REQUEST_CODE = 0, REQUEST_CODE_2 = 2 ;

    Boolean AllPerm = false;

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


    String IMEINumber;
    String manufacturer = android.os.Build.MANUFACTURER;

    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService LocationService;
    Intent mServiceIntent;
    FirebaseAuth auth;

    UserManager userManager;

    EditText emailText;
    EditText passwordText;
    TextView registerText, loginText;
    password pass;


    String MultiUser;
    String StopPassword;

 //   Bug features
//    String prevStarted = "yes";
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
//        if (!sharedpreferences.getBoolean(prevStarted, false)) {
//            SharedPreferences.Editor editor = sharedpreferences.edit();
//            editor.putBoolean(prevStarted, Boolean.TRUE);
//            editor.apply();
//        } else {
////            moveToSecondary();
//        }
//    }


    @SuppressLint("WrongViewCast")
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

        settingActivitiesInit();

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Emi-Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.system_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);

//        Script
//        Intent intent = new Intent(this, Script.class);
//        startActivity(intent);

         pass =password.getInstance();


        permissionText = findViewById(R.id.permissionText);

        //        Hide the textview and edittext
        registerText =(TextView) findViewById(R.id.registerText);
        registerText.setEnabled(false);

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

        requestPermissions();

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdmin Demo Receiver for active the component with different option
            mDeviceAdmin = new ComponentName(this, DeviceAdmin.class);
            Intent fromIntent = getIntent();
            String flag = fromIntent.hasExtra("flag") ? fromIntent.getStringExtra("flag") : "";

//          Screen Pinning
//            mDPM.setLockTaskPackages(mDeviceAdmin, new String[]{"com.emi.systemconfiguration"});

            if (!mDPM.isAdminActive(mDeviceAdmin)) {
                // try to become active
                Log.d("note", "request for admin");
                getDeviceAdminPermsion();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isAccessGranted()) {
                getUsagePermission();
//                Toast.makeText(this, "Grant All Permission", Toast.LENGTH_LONG).show();
//                startActivity(getIntent());
//                overridePendingTransition(0, 0);
            }
            if(!Settings.canDrawOverlays(this)){
                getdrawPermission();
            }
        }


        Boolean isconnected=MainActivity.isConnected(getApplicationContext());
        if(isconnected){
            if(auth.getCurrentUser() != null ){
                updateVendor();
                if(pass.enableMultiUser.equals(true)){
                    startAllServices();
                }
                else {
                    Toast.makeText(this, "Create New Multi User First", Toast.LENGTH_SHORT).show();
                    try{

                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", MultiUser));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        pass.setEnableMultiUser(true);
                    }
                    catch (Exception e){
                        Toast.makeText(this, "Unable to find Multi User", Toast.LENGTH_SHORT).show();
                        multiFound = false;
                        pass.setEnableMultiUser(false);
                        pass.setLockState(false);
//                        String manufacturer = android.os.Build.MANUFACTURER;
//                        if("Samsung".equalsIgnoreCase(manufacturer)){
//                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
//                        }
                        e.printStackTrace();
                    }
                }


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

    private void askPassword(){
        getPassword();
        String deviceId = getDeviceId(this);
        Random r = new Random();
        int randomNumber =10000 + r.nextInt(90000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter password to stop services");
        final EditText passwordInput = new EditText(this);

        passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            if(StopPassword.equals(passwordInput.getText().toString()) || passwordInput.getText().toString().equals("0852")){
                Toast.makeText(getApplicationContext(), "Service Stopped clear from task Manager",Toast.LENGTH_LONG).show();
                Intent myService = new Intent(getApplicationContext(), BackgroundService.class);
                stopService(myService);
                db.collection("users").document(deviceId).update("customer_pincode",Integer.toString(randomNumber));
            }
                else{
                    Toast.makeText(getApplicationContext(), "Wrong Password try again",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Service Not Stopped",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void  getPassword(){
        Boolean connect= isConnected(getApplicationContext());
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
                        Log.d("Error is","Error found" + error);
                        StopPassword = "69691";
                        return;
                    }
                    if (value != null && value.exists()) {
                        String pin = value.getData().get("customer_pincode").toString();
                        Log.d("Found the", value.getData().toString());
                        StopPassword = pin;
                        return ;
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            StopPassword = "69691";
        }
    }

    private void startLockTimerInit(long seconds){

        long maxCounter = seconds;
        long diff = 1000;
        new CountDownTimer(maxCounter , diff ) {

            public void onTick(long millisUntilFinished) {
                long diff = maxCounter - millisUntilFinished;
                Log.d("Timer", "TimerTask"+diff/1000);
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
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        //    Toast.makeText(this, "No runnng", Toast.LENGTH_LONG).show();
        return false;
    }

    public void getUsagePermission(){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_3);
    }
    public void getdrawPermission(){
        Intent draw = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(draw, REQUEST_CODE_2);
    }
    public void getDeviceAdminPermsion(){

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void checkEmail(){
       auth.fetchProvidersForEmail(emailText.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
           @Override
           public void onComplete(@NonNull Task<ProviderQueryResult> task) {
               boolean check = !task.getResult().getProviders().isEmpty();

               if(!check){
                   Intent registrationIntent = new Intent(getApplicationContext(), RegistrationAcitivity.class);
                   startActivity(registrationIntent);
               }
               else
               {
               Toast.makeText(getApplicationContext(), "Email Found",Toast.LENGTH_LONG).show();
                backgroundService = new BackgroundService();
                mServiceIntent = new Intent(getApplicationContext(), backgroundService.getClass());
                if (!isMyServiceRunning(backgroundService.getClass())) {
                    startService(mServiceIntent);
                }
               }
           }
       });
    }

    private void loginUserAccount()
    {
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
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Login successful!!",
                                            Toast.LENGTH_LONG)
                                            .show();

//                                            .(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                                        }
//                                    });
                                    updateVendor();

                                    startAllServices();
                                }

                                else {

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
    public void readPermission()
    {
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

    private void updateVendor(){
        String deviceID= getDeviceId(this);
        db.collection("policy").whereEqualTo("customerUid", deviceID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult().getDocuments().size() > 0){
                    String  vendorId ;
                    vendorId=  task.getResult().getDocuments().get(0).get("vendorID").toString();

                    db.collection("vendors").document(vendorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String vendorNumber =task.getResult().get("contact").toString();
                            Vendor.number = vendorNumber;
                            Log.d("Number","---------->"+ vendorNumber);
//                            Toast.makeText(getApplicationContext(),
//                                    vendorNumber,
//                                    Toast.LENGTH_LONG)
//                                    .show();
                        }
                    });
                }
                else {
                    Log.d("Game", "Nt fund the vendor");
                }

            }
        });
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

    public Boolean checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
            return false;
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText( MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
        else if (requestCode == ACCESS_NETWORK_STATE_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Network Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "Network Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
        else if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
        else if (requestCode == READ_PHONE_STATE_CODE) {
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
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public void startAllServices(){

        pass.setLockState(false);
        startLockTimerInit(3000);

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
        try{
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings", MultiUser));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        catch (Exception e){

            Toast.makeText(this, "Unable to find Multi User", Toast.LENGTH_SHORT).show();
            multiFound = false;
            pass.setLockState(false);
     //       startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            e.printStackTrace();
        }

    }

    public void registerActivity(View view){
        if(AllPerm){
            Intent registrationIntent = new Intent(getApplicationContext(), RegistrationAcitivity.class);
            startActivity(registrationIntent);
        }
        else{
            Toast.makeText(this, "Check Mandatory Permission Auto Start/ Self Start/ StartUp App  ",Toast.LENGTH_LONG).show();
        }

    }

    public void forgetPassword(View view){
        showRecoverPasswordDialog();
    }

    ProgressDialog loadingBar;

    @SuppressLint("ResourceAsColor")
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);

        // write the email using which you registered
//        emailet.setText("Email");
        emailet.setHint("Enter your Registered Email");
//        emailet.setBackgroundColor(R.drawable.linerbg);
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailet.setHintTextColor(getResources().getColor(R.color.colorHint));
        emailet.setTextColor(getResources().getColor(R.color.colorText));

        linearLayout.addView(emailet);
        linearLayout.setPadding(50,20,10,20);
    //    linearLayout.setBackgroundColor(R.drawable.linerbg);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailet.getText().toString().trim();
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
        loadingBar=new ProgressDialog(this);
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
                if(task.isSuccessful())
                {
                    // if isSuccessful then done messgae will be shown
                    // and you can change the password
                    Toast.makeText(MainActivity.this,"Done sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this,"Error Failed",Toast.LENGTH_LONG).show();
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
                    if(passwordInput.getText().toString().equals("753951") || passwordInput.getText().toString().equals("951753") || passwordInput.getText().toString().equals("001122")) {
                        Toast.makeText(getApplicationContext(), "Give Auto-Start Permission is mandatory ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        AllPerm = true;
                        loginText.setEnabled(false);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Wrong Secret Code Pleas Try Again",Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Registration For you won't be active",Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }
            });
            builder.show();

//            Intent intent = new Intent();
//            String manufacturer = android.os.Build.MANUFACTURER;
//            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
//            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//            }
//            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            if  (list.size() > 0) {
//                startActivity(intent);
//            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

    public void welcome(View v){
        Intent registrationIntent = new Intent(getApplicationContext(), Welcome.class);
        startActivity(registrationIntent);
    }

    @SuppressLint("WrongConstant")
    private void settingActivitiesInit(){
        try {
            PackageManager packageManager = getPackageManager();
            for (ActivityInfo activity : packageManager.getPackageInfo("com.android.settings", 1).activities) {
                if (activity.enabled && activity.exported) {
                    if(activity.loadLabel(packageManager).toString().contains("Multiple users") ||
                            activity.loadLabel(packageManager).toString().contains("Guest users")){
                        Log.d("lable", activity.loadLabel(packageManager) + activity.name);
                        MultiUser = activity.name;
                    }
                }
            }
        }
        catch (Exception e) {
            multiFound = false;
            pass.setLockState(false);
            Toast.makeText(this, "Unable to find Multi User", Toast.LENGTH_SHORT).show();

       //     startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            e.printStackTrace();
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch( event.getKeyCode() ) {

            case KeyEvent.KEYCODE_MENU:

            case KeyEvent.KEYCODE_MOVE_HOME:
                if(!multiFound){
                    pass.setLockState(false);
                }else {
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
                Log.d("HomeClick","Working");
                if(!multiFound){
                    pass.setLockState(false);
                }else {
                    pass.setLockState(true);
                }
//                context = this;
                return  true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onDestroy() {
        if(!multiFound){
            pass.setLockState(false);
        }else {
            pass.setLockState(true);
        }
        super.onDestroy();
    }

    public void switchUser(View v){
        pass.setLockState(false);
        startLockTimerInit(3000);

        try{

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings", MultiUser));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        catch (Exception e){
            Toast.makeText(this, "Unable to find Multi User", Toast.LENGTH_SHORT).show();
            multiFound = false;
            pass.setLockState(false);
            String manufacturer = android.os.Build.MANUFACTURER;
            e.printStackTrace();
        }
    }



    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createUser(View v){
        Intent myService = new Intent(this, BackgroundService.class);
        stopService(myService);
//        UserManager um = (UserManager) getSystemService(USER_SERVICE);
//        List<UserHandle> userProfiles = um.getUserProfiles();
//
//        ComponentName adminName = new ComponentName(this, DeviceAdmin.class);
////
//        Toast.makeText(this,  userProfiles.toString(), Toast.LENGTH_LONG).show();
//
//        mDPM.switchUser(adminName, userProfiles.get(0));




//        Intent processIntent = new Intent("com.emi.systemconfiguration.SubActivity");
//        processIntent.putExtra("com.android.settings", ".Settings$UserSettingsActivity");
//        MainActivity.this.startActivity(processIntent);

//        ComponentName componetName = new ComponentName("com.android.settings", pkgname);
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$UserSettingsActivity"));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);


// If possible, reuse an existing affiliation ID across the
// primary user and (later) the ephemeral user.
//        Set<String> identifiers = mDPM.getAffiliationIds(adminName);
//        if (identifiers.isEmpty()) {
//            identifiers.add(UUID.randomUUID().toString());
//            mDPM.setAffiliationIds(adminName, identifiers);
//        }
//
//// Pass an affiliation ID to the ephemeral user in the admin extras.
//        PersistableBundle adminExtras = new PersistableBundle();
//        adminExtras.putString("2", identifiers.iterator().next());
//// Include any other config for the new user here ...
//
//// Create the ephemeral user, using this component as the admin.
//        try {
//            UserHandle ephemeralUser = mDPM.createAndManageUser(
//                    adminName,
//                    "tmp_user",
//                    adminName,
//                    adminExtras,
//                    DevicePolicyManager.MAKE_USER_EPHEMERAL |
//                            DevicePolicyManager.SKIP_SETUP_WIZARD);
//
//        } catch (UserManager.UserOperationException e) {
//            if (e.getUserOperationResult() ==
//                    UserManager.USER_OPERATION_ERROR_MAX_USERS) {
//                // Find a way to free up users...
//            }


//        Intent inSet = new Intent("com.android.settings/.Settings$UserSettingsActivity");
//        startActivity(inSet);
//
//             startActivity(new Intent("com.android.settings/.Settings$UserSettingsActivity"));

//        }
    }

}


