/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emi.srsSystemConfiguration.provision;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_MODE;
import static android.app.admin.DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.emi.srsSystemConfiguration.R;

/**
 * Activity that gets launched by the
 * {@link android.app.admin.DevicePolicyManager#ACTION_GET_PROVISIONING_MODE} intent.
 */
@SuppressLint("NewApi")
public class GetProvisioningModeActivity extends Activity {

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.activity_get_provisioning_mode);
    final LinearLayout layout = findViewById(R.id.dpc_login);
    showRelevantProvisioningOptions(layout);
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_CANCELED);
    super.onBackPressed();
  }

  private void showRelevantProvisioningOptions(ViewGroup container) {
      showDoOption(container);
  }

  private void showDoOption(ViewGroup container) {
    container.findViewById(R.id.do_option).setVisibility(View.VISIBLE);
    container.findViewById(R.id.do_selection_button).setOnClickListener(this::onDoButtonClick);
  }

  private void onDoButtonClick(View button) {
    final Intent intent = new Intent();
    intent.putExtra(EXTRA_PROVISIONING_MODE, PROVISIONING_MODE_FULLY_MANAGED_DEVICE);
    finishWithIntent(intent);
  }

  private void finishWithIntent(Intent intent) {
    setResult(RESULT_OK, intent);
    finish();
  }
}
