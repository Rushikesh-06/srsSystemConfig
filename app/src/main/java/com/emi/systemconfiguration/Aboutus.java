package com.emi.systemconfiguration;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

//import libs.mjn.scaletouchlistener.ScaleTouchListener;

public class Aboutus extends AppCompatActivity {

    ImageView gmail,instagram;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        gmail = findViewById(R.id.gmail);
        instagram = findViewById(R.id.instagram);

//        gmail.setOnTouchListener(new ScaleTouchListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "suppport@elitnotch.com"));
//                intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
//                intent.putExtra(Intent.EXTRA_TEXT, "your_text");
//                startActivity(intent);
//            }
//        });
//
//        instagram.setOnTouchListener(new ScaleTouchListener() {
//            @Override
//            public void onClick(View v) {
//                Intent instagram = openInstagram(Aboutus.this);
//                startActivity(instagram);
//            }
//        });

    }

    public static Intent openInstagram(Context context){
        try{
            context.getPackageManager().getPackageInfo("com.instagram",0);
            return  new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/elit.notch"));
        }
        catch (Exception e){
            return  new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/elit.notch"));
        }
    }

    public static Intent openGmail(Context context){
        try{
            context.getPackageManager().getPackageInfo("com.gmail",0);
            return  new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/"));
        }
        catch (Exception e){
            return  new Intent(Intent.ACTION_VIEW,Uri.parse("mailto:support@elitnotch.com"));
        }
    }

//    ScaleTouchListener.Config config = new ScaleTouchListener.Config(
//        300,
//            0.85f,
//            0.85f
//    );


}
