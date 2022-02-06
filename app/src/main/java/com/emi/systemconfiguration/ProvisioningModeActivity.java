package com.emi.systemconfiguration;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ProvisioningModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        int provisioningMode = 1;
        List<Integer> allowedProvisioningModes = intent.getIntegerArrayListExtra(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE);

        if (allowedProvisioningModes.contains(DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE))
            provisioningMode = DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE;
        else if (allowedProvisioningModes.contains(DevicePolicyManager.PROVISIONING_MODE_MANAGED_PROFILE))
            provisioningMode = DevicePolicyManager.PROVISIONING_MODE_MANAGED_PROFILE;

        Intent resultIntent = new Intent();
        resultIntent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_MODE, provisioningMode);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}