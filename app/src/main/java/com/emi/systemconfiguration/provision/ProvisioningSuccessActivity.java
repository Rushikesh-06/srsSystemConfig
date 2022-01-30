package com.emi.systemconfiguration.provision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


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
//            Toast.makeText(this, C0740R.string.device_admin_receiver_failure, 1).show();
        }
        finish();
    }
}
