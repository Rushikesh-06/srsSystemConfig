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

    }

}
