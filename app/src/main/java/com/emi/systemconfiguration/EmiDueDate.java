package com.emi.systemconfiguration;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import java.util.ArrayList;

public class EmiDueDate extends AppCompatActivity {

    GridView coursesGV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final Window win= getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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


        ArrayList<InfoModel> itemModelArrayList = new ArrayList<InfoModel>();
        itemModelArrayList.add(new InfoModel(Build.MANUFACTURER.toString(), R.drawable.ic_launcher_foreground));
        itemModelArrayList.add(new InfoModel("JAVA", R.drawable.goelctronixc));
        itemModelArrayList.add(new InfoModel("C++", R.drawable.system_icon));
        itemModelArrayList.add(new InfoModel("Python", R.drawable.system_icon));
        itemModelArrayList.add(new InfoModel("Javascript", R.drawable.system_icon));
        itemModelArrayList.add(new InfoModel("DSA", R.drawable.ic_launcher_foreground));

        InfoAdapter adapter = new InfoAdapter(this, itemModelArrayList);
        coursesGV.setAdapter(adapter);

    }


}
