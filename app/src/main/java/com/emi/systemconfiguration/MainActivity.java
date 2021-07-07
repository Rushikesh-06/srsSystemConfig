package com.emi.systemconfiguration;

import androidx.annotation.NonNull;
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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
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

import static android.service.controls.ControlsProviderService.TAG;

public class MainActivity extends AppCompatActivity {

    public ComponentName mDeviceAdmin;
    public DevicePolicyManager mDPM;
    public TextView mToggleAdminBtn;
    public static final int REQUEST_CODE =0;

    Button checkEmailBtn;
    TextView permissionText;


    //For Permission
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int  ACCESS_NETWORK_STATE_CODE= 102;
    private static final int PACKAGE_USAGE_STATS_CODE= 103;
    private static final int ACCESS_FINE_LOCATION_CODE= 104;
    private static final int READ_PHONE_STATE_CODE= 105;



    String IMEINumber;
    String manufacturer = android.os.Build.MANUFACTURER;

    private BackgroundService backgroundService;
    private BackgroundDelayService backgroundDelayService;
    private LocationService LocationService;
    Intent mServiceIntent;
    FirebaseAuth auth;

    EditText emailText;
    EditText passwordText;
    TextView registerText;

//    Bug features
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
        emailText =(EditText) findViewById(R.id.emailId);
        passwordText =(EditText) findViewById(R.id.editTextPassword);


        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Emi-Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.system_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar
//
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        permissionText = findViewById(R.id.permissionText);

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
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);

            }
//            Intent intent = new Intent();
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
//
//            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            if (list.size() > 0) {
//                startActivity(intent);
//            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!isAccessGranted() && !Settings.canDrawOverlays(this) || !Settings.canDrawOverlays(this) || !isAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                Intent draw = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(draw, 0);
                Toast.makeText(this, "Grant All Permission", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            } else {

                requestPermissions();
//                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
//                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);
//                checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, ACCESS_NETWORK_STATE_CODE);
//                checkPermission(Manifest.permission.PACKAGE_USAGE_STATS, PACKAGE_USAGE_STATS_CODE);
//                checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE_CODE);

//                emailText.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        if(s.toString().contains("@") && s.toString().contains(".")){
////                            checkEmail();
//                        }
//                    }
//                });

//
//                Intent registrationIntent = new Intent(this, RegistrationAcitivity.class);
//                startActivity(registrationIntent);
                //    New Bgservice 3
//                mYourService = new BackgroundService();
//                mServiceIntent = new Intent(this, mYourService.getClass());
//                if (!isMyServiceRunning(mYourService.getClass())) {
//                    startService(mServiceIntent);
//                }


            }

        }
        Boolean isconnected=MainActivity.isConnected(getApplicationContext());
        if(isconnected){
            if(auth.getCurrentUser() != null ){
//            Intent intent = new Intent(this, EmiDueDate.class);
//            startActivity(intent);
//            finish();

                startAllServices();

            }
        }

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

                                    startAllServices();
                                    // hide the progress bar


                                    // if sign-in is successful
                                    // intent to home activity
//                                    Intent intent
//                                            = new Intent(LoginActivity.this,
//                                            MainActivity.class);
//                                    startActivity(intent);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE) {
//            Log.d("note", String.valueOf(resultCode));
//            switch (resultCode) {
//                // End user cancels the request
//                case Activity.RESULT_CANCELED:
//                    Log.d("note","Cancled");
////                    startService(new Intent(this, BackgroundService.class));
//                    break;
//                // End user accepts the request
//                case Activity.RESULT_OK:
//                    Log.d("note","Actvated");
////                    stopService(new Intent(this, BackgroundService.class));
////                    Log.d("note",getResources().getString(R.string.admin_activated));
////                    refreshButtons();
//                    break;
////                case Activity.DEL
//                default:
//                    Log.d("note", "default");
//
//            }
//        }
//    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void  getUniqueIMEIId(View view) {
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
//            return;
//        }
//        RegistrationAcitivity register = new RegistrationAcitivity();
//        register.getPolicyIdList(this);
//
//        IMEINumber = getDeviceId(this);
//        emailText.setText(IMEINumber);
        Toast.makeText(this, "Location Set!", Toast.LENGTH_LONG).show();



//        Intent intent = new Intent(MainActivity.this, CountBroad.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//        AlarmManager alarmManager =(AlarmManager) getSystemService(ALARM_SERVICE);
//        long timeAtButtonclick = System.currentTimeMillis();
//        long tenseconds = 1000 * 1;
//
//        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonclick + tenseconds, pendingIntent);

//        Calendar calendar = Calendar.getInstance();
//
//        calendar.set(Calendar.HOUR_OF_DAY, 18);
//        calendar.set(Calendar.MINUTE, 40);
//        calendar.set(Calendar.SECOND, 0);
//        Intent intent1 = new Intent(getApplicationContext(), CountBroad.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100,intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);


    }

//    private void createNotficationchannel(){
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            CharSequence name ="AntiTheft-Locker";
//            String description = "Due date for Emi-locker";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("ApptheftLocker", name, importance);
//            channel.setDescription(description);
//
//        }
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_CODE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

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

//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
//
//            }
//            else {
//                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
//            }
//        }
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

    @SuppressLint("InlinedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Dexter.withActivity(this)
                    // below line is use to request the number of
                    // permissions which are required in our app.
                    .withPermissions(Manifest.permission.CAMERA,
                            // below is the list of permissions
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.FOREGROUND_SERVICE,
                            Manifest.permission.ACCESS_FINE_LOCATION

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
                            // check for permanent denial of any permission
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
        Toast.makeText(this, "All service started successfully don't need to login", Toast.LENGTH_LONG).show();
    }

    public void registerActivity(View view){

        Intent registrationIntent = new Intent(getApplicationContext(), RegistrationAcitivity.class);
        startActivity(registrationIntent);

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

}


