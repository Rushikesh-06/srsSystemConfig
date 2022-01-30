package com.emi.systemconfiguration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AdminPolicyCompliance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_policy_compliance);

        setResult(RESULT_OK);
        finish();
    }
}
