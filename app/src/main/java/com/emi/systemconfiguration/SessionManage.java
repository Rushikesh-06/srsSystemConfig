package com.emi.systemconfiguration;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManage {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public SessionManage(Context context) {
        preferences = context.getSharedPreferences("EMILOCKER", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }




    public boolean getregisteredStatus() {
        return preferences.getBoolean("Is_registered", false);
    }

    public void  addregisteredstatus(boolean status) {
        editor.putBoolean("Is_registered", status);
        editor.commit();
    }
}
