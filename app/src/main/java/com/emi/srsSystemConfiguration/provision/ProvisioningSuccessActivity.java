package com.emi.srsSystemConfiguration.provision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class ProvisioningSuccessActivity extends Activity {
    private static final String TAG = "ProvisioningSuccess";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.e("EnableStart", "ProvisioningSuccessActivity");
        PostProvisioningTask postProvisioningTask = new PostProvisioningTask(this);
        if (!postProvisioningTask.performPostProvisioningOperations(getIntent())) {
            finish();
            return;
        }
        Intent postProvisioningLaunchIntent = postProvisioningTask.getPostProvisioningLaunchIntent(getIntent());
        if (postProvisioningLaunchIntent != null) {
            startActivity(postProvisioningLaunchIntent);
        } else {
            Log.e(TAG, "ProvisioningSuccessActivity.onCreate() invoked, but ownership not assigned");

        }

        finish();
    }
}
