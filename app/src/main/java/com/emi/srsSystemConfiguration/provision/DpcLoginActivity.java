package com.emi.srsSystemConfiguration.provision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.emi.srsSystemConfiguration.R;


public class DpcLoginActivity extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dpc_login);
        Log.e("EnableStart", "DpcLoginActivity");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dpc_login);
        linearLayout.findViewById(R.id.do_selection_button).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DpcLoginActivity.this.onDoButtonClick(view);
            }
        });
        linearLayout.findViewById(R.id.po_selection_button).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DpcLoginActivity.this.onPoButtonClick(view);
            }
        });
    }

    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    /* access modifiers changed from: private */
    public void onDoButtonClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("android.app.extra.PROVISIONING_MODE", 1);
        finishWithIntent(intent);
    }

    /* access modifiers changed from: private */
    public void onPoButtonClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("android.app.extra.PROVISIONING_MODE", 2);
        finishWithIntent(intent);
    }

    private void finishWithIntent(Intent intent) {
        setResult(-1, intent);
        finish();
    }
}
