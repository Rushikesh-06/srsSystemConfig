package com.emi.systemconfiguration.common;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;

public class LaunchIntentUtil {
    private static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_ACCOUNT_NAME = "account_name";
    public static final String EXTRA_AFFILIATION_ID = "affiliation_id";
    private static final String EXTRA_IS_SETUP_WIZARD = "is_setup_wizard";

    private LaunchIntentUtil() {
    }

    public static boolean isSynchronousAuthLaunch(Intent intent) {
        return (intent == null || intent.getExtras() == null || intent.getExtras().get(EXTRA_IS_SETUP_WIZARD) == null) ? false : true;
    }

    public static boolean isSynchronousAuthLaunch(PersistableBundle persistableBundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return (persistableBundle == null || persistableBundle.get(EXTRA_IS_SETUP_WIZARD) == null) ? false : true;
        }
        return false;
    }

    public static Account getAddedAccount(Intent intent) {
        if (intent != null) {
            return (Account) intent.getParcelableExtra(EXTRA_ACCOUNT);
        }
        return null;
    }

    public static String getAddedAccountName(PersistableBundle persistableBundle) {
        if (persistableBundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return persistableBundle.getString(EXTRA_ACCOUNT_NAME, (String) null);
            }
        }
        return null;
    }

    public static void prepareDeviceAdminExtras(Intent intent, PersistableBundle persistableBundle, Activity activity) {
        if (isSynchronousAuthLaunch(intent)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                persistableBundle.putString(EXTRA_IS_SETUP_WIZARD, Boolean.toString(intent.getBooleanExtra(EXTRA_IS_SETUP_WIZARD, false)));
            }
            Account addedAccount = getAddedAccount(intent);
            if (addedAccount != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    persistableBundle.putString(EXTRA_ACCOUNT_NAME, addedAccount.name);
                }
            }
        }
    }
}
