package com.emi.systemconfiguration;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class EmiDueDate extends AppCompatActivity {

    GridView coursesGV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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

}
