package com.emi.srsSystemConfiguration.provision;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CheckInState {
    public static final String FIRST_ACCOUNT_READY_PROCESSED_ACTION = "com.financelocker.FIRST_ACCOUNT_READY_PROCESSED";
    public static final int FIRST_ACCOUNT_STATE_PENDING = 0;
    public static final int FIRST_ACCOUNT_STATE_READY = 1;
    public static final int FIRST_ACCOUNT_STATE_TIMEOUT = 2;
    private static final String KEY_FIRST_ACCOUNT_STATE = "first_account_state";
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public CheckInState(Context context) {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context.getApplicationContext();
    }

    public int getFirstAccountState() {
        return this.mSharedPreferences.getInt(KEY_FIRST_ACCOUNT_STATE, 0);
    }

    public void setFirstAccountState(int i) {
        this.mSharedPreferences.edit().putInt(KEY_FIRST_ACCOUNT_STATE, i).apply();
        if (i != 0) {
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(new Intent(FIRST_ACCOUNT_READY_PROCESSED_ACTION));
        }
    }
}
