package com.emi.systemconfiguration;

import android.util.Log;

public class GlobalLock {
    Boolean lock = false;
    private static final GlobalLock ourInstance = new GlobalLock();
    public static GlobalLock getInstance() {
        return ourInstance;
    }
    private GlobalLock() {
    }
    public void setData(Boolean lock) {
        this.lock = lock;
    }
    public Boolean getData() {
        return lock;
    }
}
