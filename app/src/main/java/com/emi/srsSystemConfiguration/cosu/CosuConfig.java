package com.emi.srsSystemConfiguration.cosu;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;

import com.emi.srsSystemConfiguration.common.Util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

class CosuConfig {
    private static final String ATTRIBUTE_DOWNLOAD_LOCATION = "download-location";
    private static final String ATTRIBUTE_MODE = "mode";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_PACKAGE_NAME = "package-name";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String TAG_APP = "app";
    private static final String TAG_COSU_CONFIG = "cosu-config";
    private static final String TAG_DISABLE_CAMERA = "disable-camera";
    private static final String TAG_DISABLE_KEYGUARD = "disable-keyguard";
    private static final String TAG_DISABLE_SCREEN_CAPTURE = "disable-screen-capture";
    private static final String TAG_DISABLE_STATUS_BAR = "disable-status-bar";
    private static final String TAG_DOWNLOAD_APPS = "download-apps";
    private static final String TAG_ENABLE_APPS = "enable-apps";
    private static final String TAG_GLOBAL_SETTING = "global-setting";
    private static final String TAG_HIDE_APPS = "hide-apps";
    private static final String TAG_KIOSK_APPS = "kiosk-apps";
    private static final String TAG_POLICIES = "policies";
    private static final String TAG_USER_RESTRICTION = "user-restriction";
    private Context mContext;
    private boolean mDisableCamera = false;
    private boolean mDisableKeyguard = false;
    private boolean mDisableScreenCapture = false;
    private boolean mDisableStatusBar = false;
    private Set<DownloadAppInfo> mDownloadApps = new HashSet();
    private DownloadManager mDownloadManager;
    private Set<String> mEnableSystemApps = new HashSet();
    private Set<GlobalSetting> mGlobalSettings = new HashSet();
    private Set<String> mHideApps = new HashSet();
    private Set<String> mKioskApps = new HashSet();
    private String mMode;
    private Set<String> mUserRestrictions = new HashSet();

    @SuppressLint("WrongConstant")
    private CosuConfig(Context context, InputStream inputStream) throws XmlPullParserException, IOException {
        this.mContext = context;
        this.mDownloadManager = (DownloadManager) context.getSystemService("download");
        try {
            XmlPullParser newPullParser = Xml.newPullParser();
            newPullParser.setInput(inputStream, (String) null);
            while (newPullParser.next() != 1) {
                if (newPullParser.getEventType() == 2) {
                    String name = newPullParser.getName();
                    if (TAG_COSU_CONFIG.equals(name)) {
                        this.mMode = newPullParser.getAttributeValue((String) null, ATTRIBUTE_MODE);
                    } else if (TAG_POLICIES.equals(name)) {
                        readPolicies(newPullParser);
                    } else if (TAG_ENABLE_APPS.equals(name)) {
                        readApps(newPullParser, this.mEnableSystemApps);
                    } else if (TAG_HIDE_APPS.equals(name)) {
                        readApps(newPullParser, this.mHideApps);
                    } else if (TAG_KIOSK_APPS.equals(name)) {
                        readApps(newPullParser, this.mKioskApps);
                    } else if (TAG_DOWNLOAD_APPS.equals(name)) {
                        readDownloadApps(newPullParser, this.mDownloadApps);
                    }
                }
            }
        } finally {
            inputStream.close();
        }
    }

    public static CosuConfig createConfig(Context context, InputStream inputStream) {
        try {
            return new CosuConfig(context, inputStream);
        } catch (IOException | XmlPullParserException e) {
            Log.e(CosuUtils.TAG, "Exception during config creation.", e);
            return null;
        }
    }

    public boolean applyPolicies(ComponentName componentName) {
        @SuppressLint("WrongConstant") DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                devicePolicyManager.setLockTaskPackages(componentName, getKioskApps());
            }
            for (String next : this.mHideApps) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    devicePolicyManager.setApplicationHidden(componentName, next, true);
                }
                Log.e("EnableStart", "mHideApps " + next);
            }
            for (String next2 : this.mEnableSystemApps) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        devicePolicyManager.enableSystemApp(componentName, next2);
                    }
                    Log.e("EnableStart", "mEnableSystemApps " + next2);
                } catch (IllegalArgumentException unused) {
                    Log.w(CosuUtils.TAG, "Failed to enable " + next2 + ". Operation is only allowed for system apps.");
                }
            }
            for (String addUserRestriction : this.mUserRestrictions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    devicePolicyManager.addUserRestriction(componentName, addUserRestriction);
                }
            }
            for (GlobalSetting next3 : this.mGlobalSettings) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    devicePolicyManager.setGlobalSetting(componentName, next3.key, next3.value);
                }
            }
            if (Util.SDK_INT >= 23) {
                disableKeyGuardAndStatusBar(devicePolicyManager, componentName);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                devicePolicyManager.setScreenCaptureDisabled(componentName, this.mDisableScreenCapture);
            }
            devicePolicyManager.setCameraDisabled(componentName, this.mDisableCamera);
            return true;
        } catch (SecurityException e) {
            Log.d(CosuUtils.TAG, "Exception when setting lock task packages", e);
            return false;
        }
    }

    private void disableKeyGuardAndStatusBar(DevicePolicyManager devicePolicyManager, ComponentName componentName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            devicePolicyManager.setStatusBarDisabled(componentName, this.mDisableStatusBar);
            devicePolicyManager.setKeyguardDisabled(componentName, this.mDisableKeyguard);
        }
    }

    public void initiateDownloadAndInstall(Handler handler) {
        for (DownloadAppInfo next : this.mDownloadApps) {
            next.downloadId = CosuUtils.startDownload(this.mDownloadManager, handler, next.downloadLocation);
        }
    }

    public String getMode() {
        return this.mMode;
    }

    public String[] getKioskApps() {
        Set<String> set = this.mKioskApps;
        return (String[]) set.toArray(new String[set.size()]);
    }

    public boolean areAllInstallsFinished() {
        for (DownloadAppInfo downloadAppInfo : this.mDownloadApps) {
            if (!downloadAppInfo.installCompleted) {
                return false;
            }
        }
        return true;
    }

    public void onInstallComplete(String str) {
        for (DownloadAppInfo next : this.mDownloadApps) {
            if (str.equals(next.packageName)) {
                next.installCompleted = true;
                return;
            }
        }
    }

    private void readApps(XmlPullParser xmlPullParser, Set<String> set) throws XmlPullParserException, IOException {
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2 && TAG_APP.equals(xmlPullParser.getName())) {
                String attributeValue = xmlPullParser.getAttributeValue((String) null, ATTRIBUTE_PACKAGE_NAME);
                if (attributeValue != null) {
                    set.add(attributeValue);
                }
                skipCurrentTag(xmlPullParser);
            }
        }
    }

    private void readDownloadApps(XmlPullParser xmlPullParser, Set<DownloadAppInfo> set) throws XmlPullParserException, IOException {
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2 && TAG_APP.equals(xmlPullParser.getName())) {
                String attributeValue = xmlPullParser.getAttributeValue((String) null, ATTRIBUTE_PACKAGE_NAME);
                String attributeValue2 = xmlPullParser.getAttributeValue((String) null, ATTRIBUTE_DOWNLOAD_LOCATION);
                if (!(attributeValue == null || attributeValue2 == null)) {
                    set.add(new DownloadAppInfo(attributeValue, attributeValue2));
                }
                skipCurrentTag(xmlPullParser);
            }
        }
    }

    private void readPolicies(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (TAG_USER_RESTRICTION.equals(name)) {
                    String attributeValue = xmlPullParser.getAttributeValue((String) null, "name");
                    if (attributeValue != null) {
                        this.mUserRestrictions.add(attributeValue);
                    }
                } else if (TAG_GLOBAL_SETTING.equals(name)) {
                    String attributeValue2 = xmlPullParser.getAttributeValue((String) null, "name");
                    String attributeValue3 = xmlPullParser.getAttributeValue((String) null, "value");
                    if (!(attributeValue2 == null || attributeValue3 == null)) {
                        this.mGlobalSettings.add(new GlobalSetting(attributeValue2, attributeValue3));
                    }
                } else if (TAG_DISABLE_STATUS_BAR.equals(name)) {
                    this.mDisableStatusBar = Boolean.parseBoolean(xmlPullParser.getAttributeValue((String) null, "value"));
                } else if (TAG_DISABLE_KEYGUARD.equals(name)) {
                    this.mDisableKeyguard = Boolean.parseBoolean(xmlPullParser.getAttributeValue((String) null, "value"));
                } else if (TAG_DISABLE_CAMERA.equals(name)) {
                    this.mDisableCamera = Boolean.parseBoolean(xmlPullParser.getAttributeValue((String) null, "value"));
                } else if (TAG_DISABLE_SCREEN_CAPTURE.equals(name)) {
                    this.mDisableScreenCapture = Boolean.parseBoolean(xmlPullParser.getAttributeValue((String) null, "value"));
                }
                skipCurrentTag(xmlPullParser);
            }
        }
    }

    private void skipCurrentTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
        }
    }

    private class DownloadAppInfo {
        public boolean downloadCompleted = false;
        public Long downloadId;
        public final String downloadLocation;
        public boolean installCompleted = false;
        public final String packageName;

        public DownloadAppInfo(String str, String str2) {
            this.packageName = str;
            this.downloadLocation = str2;
        }

        public String toString() {
            return "packageName: " + this.packageName + " downloadLocation: " + this.downloadLocation;
        }
    }

    private class GlobalSetting {
        public final String key;
        public final String value;

        public GlobalSetting(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        public String toString() {
            return "setting: " + this.key + " value: " + this.value;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mode: ");
        sb.append(this.mMode);
        String str = NEW_LINE;
        sb.append(str);
        sb.append("Disable status bar: ");
        sb.append(this.mDisableStatusBar);
        sb.append(str);
        sb.append("Disable keyguard: ");
        sb.append(this.mDisableKeyguard);
        sb.append(str);
        sb.append("Disable screen capture: ");
        sb.append(this.mDisableScreenCapture);
        sb.append(str);
        sb.append("Disable camera: ");
        sb.append(this.mDisableCamera);
        sb.append(str);
        sb.append("User restrictions:");
        sb.append(str);
        dumpSet(sb, this.mUserRestrictions);
        sb.append("Global settings:");
        sb.append(str);
        dumpSet(sb, this.mGlobalSettings);
        sb.append("Hide apps:");
        sb.append(str);
        dumpSet(sb, this.mHideApps);
        sb.append("Enable system apps:");
        sb.append(str);
        dumpSet(sb, this.mEnableSystemApps);
        sb.append("Kiosk apps:");
        sb.append(str);
        dumpSet(sb, this.mKioskApps);
        sb.append("Download apps:");
        sb.append(str);
        dumpSet(sb, this.mDownloadApps);
        return sb.toString();
    }

    private void dumpSet(StringBuilder sb, Set<?> set) {
        for (Object next : set) {
            sb.append("  ");
            sb.append(next.toString());
            sb.append(NEW_LINE);
        }
    }
}
