package com.emi.systemconfiguration;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class HomeReciever extends BroadcastReciever {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("Home","nbm hme alla");
        Toast.makeText(context,"Working ghhari aala",Toast.LENGTH_LONG).show();
    }
}
