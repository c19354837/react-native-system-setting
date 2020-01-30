package com.ninty.system.setting;

import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by ninty on 2017/5/29.
 */

public class SystemSetting extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {

    private String TAG = SystemSetting.class.getSimpleName();

    private static final String VOL_VOICE_CALL = "call";
    private static final String VOL_SYSTEM = "system";
    private static final String VOL_RING = "ring";
    private static final String VOL_MUSIC = "music";
    private static final String VOL_ALARM = "alarm";
    private static final String VOL_NOTIFICATION = "notification";

    private ReactApplicationContext mContext;
    private AudioManager am;
    private WifiManager wm;
    private LocationManager lm;
    private VolumeBroadcastReceiver volumeBR;
    private volatile BroadcastReceiver wifiBR;
    private volatile BroadcastReceiver bluetoothBR;
    private volatile BroadcastReceiver locationBR;
    private volatile BroadcastReceiver locationModeBR;
    private volatile BroadcastReceiver airplaneBR;

    public SystemSetting(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        am = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        wm = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lm = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        volumeBR = new VolumeBroadcastReceiver();
    }

    private void registerVolumeReceiver() {
        if (!volumeBR.isRegistered()) {
            IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
            mContext.registerReceiver(volumeBR, filter);
            volumeBR.setRegistered(true);
        }
    }

    private void unregisterVolumeReceiver() {
        if (volumeBR.isRegistered()) {
            mContext.unregisterReceiver(volumeBR);
            volumeBR.setRegistered(false);
        }
    }

    private void listenWifiState() {
        if (wifiBR == null) {
            synchronized (this) {
                if (wifiBR == null) {
                    wifiBR = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                                if (wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_DISABLED) {
                                    mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                            .emit("EventWifiChange", wifiState == WifiManager.WIFI_STATE_ENABLED);
                                }
                            }
                        }
                    };
                    IntentFilter wifiFilter = new IntentFilter();
                    wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

                    mContext.registerReceiver(wifiBR, wifiFilter);
                }
            }
        }
    }

    private void listenBluetoothState() {
        if (bluetoothBR == null) {
            synchronized (this) {
                if (bluetoothBR == null) {
                    bluetoothBR = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                                if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_OFF) {
                                    mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                            .emit("EventBluetoothChange", state == BluetoothAdapter.STATE_ON);
                                }
                            }
                        }
                    };
                    IntentFilter bluetoothFilter = new IntentFilter();
                    bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

                    mContext.registerReceiver(bluetoothBR, bluetoothFilter);
                }
            }
        }
    }

    private void listenLocationState() {
        if (locationBR == null) {
            synchronized (this) {
                if (locationBR == null) {
                    locationBR = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                                mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                        .emit("EventLocationChange", isLocationEnable());
                            }
                        }
                    };
                    IntentFilter locationFilter = new IntentFilter();
                    locationFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

                    mContext.registerReceiver(locationBR, locationFilter);
                }
            }
        }
    }

    private void listenLocationModeState() {
        if (locationModeBR == null) {
            synchronized (this) {
                if (locationModeBR == null) {
                    locationModeBR = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(LocationManager.MODE_CHANGED_ACTION)) {
                                mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                        .emit("EventLocationModeChange", getLocationMode());
                            }
                        }
                    };
                    IntentFilter locationFilter = new IntentFilter();
                    locationFilter.addAction(LocationManager.MODE_CHANGED_ACTION);

                    mContext.registerReceiver(locationModeBR, locationFilter);
                }
            }
        }
    }

    private void listenAirplaneState() {
        if (airplaneBR == null) {
            synchronized (this) {
                if (airplaneBR == null) {
                    airplaneBR = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {
                                int val = Settings.System.getInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
                                mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                        .emit("EventAirplaneChange", val == 1);
                            } catch (Settings.SettingNotFoundException e) {
                                Log.e(TAG, "err", e);
                            }
                        }
                    };
                    IntentFilter locationFilter = new IntentFilter();
                    locationFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

                    mContext.registerReceiver(airplaneBR, locationFilter);
                }
            }
        }
    }

    @Override
    public String getName() {
        return SystemSetting.class.getSimpleName();
    }

    @ReactMethod
    public void setScreenMode(int mode, Promise promise) {
        mode = mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL ? mode : Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        checkAndSet(Settings.System.SCREEN_BRIGHTNESS_MODE, mode, promise);
    }

    @ReactMethod
    public void getScreenMode(Promise promise) {
        try {
            int mode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            promise.resolve(mode);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "err", e);
            promise.reject("-1", "get screen mode fail", e);
        }
    }

    @ReactMethod
    public void setBrightness(float val, Promise promise) {
        final int brightness = (int) (val * 255);
        checkAndSet(Settings.System.SCREEN_BRIGHTNESS, brightness, promise);
    }

    @ReactMethod
    public void setAppBrightness(float val) {
        final Activity curActivity = getCurrentActivity();
        if (curActivity == null) {
            return;
        }
        final WindowManager.LayoutParams lp = curActivity.getWindow().getAttributes();
        lp.screenBrightness = val;
        curActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                curActivity.getWindow().setAttributes(lp);
            }
        });
    }

    @ReactMethod
    public void getAppBrightness(Promise promise) {
        final Activity curActivity = getCurrentActivity();
        if (curActivity == null) {
            return;
        }
        try {
            float result = curActivity.getWindow().getAttributes().screenBrightness;
            if (result < 0) {
                int val = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                promise.resolve(val * 1.0f / 255);
            } else {
                promise.resolve(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "err", e);
            promise.reject("-1", "get app's brightness fail", e);
        }
    }

    @ReactMethod
    public void openWriteSetting() {
        Intent intent = new Intent(SysSettings.WRITESETTINGS.action, Uri.parse("package:" + mContext.getPackageName()));
        mContext.getCurrentActivity().startActivity(intent);
    }

    @ReactMethod
    public void getBrightness(Promise promise) {
        try {
            int val = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            promise.resolve(val * 1.0f / 255);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "err", e);
            promise.reject("-1", "get brightness fail", e);
        }
    }

    @ReactMethod
    public void setVolume(float val, ReadableMap config) {
        unregisterVolumeReceiver();
        String type = config.getString("type");
        boolean playSound = config.getBoolean("playSound");
        boolean showUI = config.getBoolean("showUI");
        int volType = getVolType(type);
        int flags = 0;
        if (playSound) {
            flags |= AudioManager.FLAG_PLAY_SOUND;
        }
        if (showUI) {
            flags |= AudioManager.FLAG_SHOW_UI;
        }
        try {
            am.setStreamVolume(volType, (int) (val * am.getStreamMaxVolume(volType)), flags);
        } catch (SecurityException e) {
            if (val == 0) {
                Log.w(TAG, "setVolume(0) failed. See https://github.com/c19354837/react-native-system-setting/issues/48");
                NotificationManager notificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    mContext.startActivity(intent);
                }
            }
            Log.e(TAG, "err", e);
        }
        registerVolumeReceiver();
    }

    @ReactMethod
    public void getVolume(String type, Promise promise) {
        promise.resolve(getNormalizationVolume(type));
    }

    private void checkAndSet(String name, int value, Promise promise) {
        boolean reject = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(mContext)) {
            reject = true;
        } else {
            try {
                Settings.System.putInt(mContext.getContentResolver(), name, value);
                int newVal = Settings.System.getInt(mContext.getContentResolver(), name);
                if (newVal != value) {
                    reject = true;
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.e(TAG, "err", e);
                //ignore
            } catch (SecurityException e) {
                Log.e(TAG, "err", e);
                reject = true;
            }
        }
        if (reject) {
            promise.reject("-1", "write_settings permission is blocked by system");
        } else {
            promise.resolve(true);
        }
    }

    private float getNormalizationVolume(String type) {
        int volType = getVolType(type);
        return am.getStreamVolume(volType) * 1.0f / am.getStreamMaxVolume(volType);
    }

    private int getVolType(String type) {
        switch (type) {
            case VOL_VOICE_CALL:
                return AudioManager.STREAM_VOICE_CALL;
            case VOL_SYSTEM:
                return AudioManager.STREAM_SYSTEM;
            case VOL_RING:
                return AudioManager.STREAM_RING;
            case VOL_ALARM:
                return AudioManager.STREAM_ALARM;
            case VOL_NOTIFICATION:
                return AudioManager.STREAM_NOTIFICATION;
            default:
                return AudioManager.STREAM_MUSIC;
        }
    }

    @ReactMethod
    public void isWifiEnabled(Promise promise) {
        if (wm != null) {
            promise.resolve(wm.isWifiEnabled());
        } else {
            promise.reject("-1", "get wifi manager fail");
        }
    }

    @ReactMethod
    public void switchWifiSilence() {
        if (wm != null) {
            listenWifiState();
            wm.setWifiEnabled(!wm.isWifiEnabled());
        } else {
            Log.w(TAG, "Cannot get wifi manager, switchWifi will be ignored");
        }
    }

    @ReactMethod
    public void switchWifi() {
        switchSetting(SysSettings.WIFI);
    }

    @ReactMethod
    public void isLocationEnabled(Promise promise) {
        if (lm != null) {
            promise.resolve(isLocationEnable());
        } else {
            promise.reject("-1", "get location manager fail");
        }
    }

    @ReactMethod
    public void getLocationMode(Promise promise) {
        if (lm != null) {
            promise.resolve(getLocationMode());
        } else {
            promise.reject("-1", "get location manager fail");
        }
    }

    private int getLocationMode() {
        int result = 0;
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            result |= 1;
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            result |= 1 << 1;
        }
        return result;
    }

    private boolean isLocationEnable() {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @ReactMethod
    public void switchLocation() {
        switchSetting(SysSettings.LOCATION);
    }

    @ReactMethod
    public void isBluetoothEnabled(Promise promise) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        promise.resolve(bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }

    @ReactMethod
    public void switchBluetooth() {
        switchSetting(SysSettings.BLUETOOTH);
    }

    @ReactMethod
    public void switchBluetoothSilence() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            listenBluetoothState();
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
            } else {
                bluetoothAdapter.enable();
            }
        }
    }

    @ReactMethod
    public void activeListener(String type, Promise promise) {
        switch (type) {
            case "wifi":
                listenWifiState();
                promise.resolve(null);
                return;
            case "bluetooth":
                listenBluetoothState();
                promise.resolve(null);
                return;
            case "location":
                listenLocationState();
                promise.resolve(null);
                return;
            case "locationMode":
                listenLocationModeState();
                promise.resolve(null);
                return;
            case "airplane":
                listenAirplaneState();
                promise.resolve(null);
                return;
        }
        promise.reject("-1", "unsupported listener type: " + type);
    }

    @ReactMethod
    public void isAirplaneEnabled(Promise promise) {
        try {
            int val = Settings.System.getInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
            promise.resolve(val == 1);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "err", e);
            promise.reject("-1", "get airplane mode fail", e);
        }
    }

    @ReactMethod
    public void switchAirplane() {
        switchSetting(SysSettings.AIRPLANE);
    }

    private void switchSetting(SysSettings setting) {
        if (mContext.getCurrentActivity() != null) {
            mContext.addActivityEventListener(this);
            Intent intent = new Intent(setting.action);
            mContext.getCurrentActivity().startActivityForResult(intent, setting.requestCode);
        } else {
            Log.w(TAG, "getCurrentActivity() return null, switch will be ignore");
        }
    }

    @ReactMethod
    public void openAppSystemSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        SysSettings setting = SysSettings.get(requestCode);
        if (setting != SysSettings.UNKNOW) {
            mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("EventEnterForeground", null);
            mContext.removeActivityEventListener(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onHostResume() {
        registerVolumeReceiver();
    }

    @Override
    public void onHostPause() {
        unregisterVolumeReceiver();
    }

    @Override
    public void onHostDestroy() {
        if (wifiBR != null) {
            mContext.unregisterReceiver(wifiBR);
            wifiBR = null;
        }
        if (bluetoothBR != null) {
            mContext.unregisterReceiver(bluetoothBR);
            bluetoothBR = null;
        }
        if (locationBR != null) {
            mContext.unregisterReceiver(locationBR);
            locationBR = null;
        }
        if (locationModeBR != null) {
            mContext.unregisterReceiver(locationModeBR);
            locationBR = null;
        }
        if (airplaneBR != null) {
            mContext.unregisterReceiver(airplaneBR);
            airplaneBR = null;
        }
    }

    private class VolumeBroadcastReceiver extends BroadcastReceiver {

        private boolean isRegistered = false;

        public void setRegistered(boolean registered) {
            isRegistered = registered;
        }

        public boolean isRegistered() {
            return isRegistered;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                WritableMap para = Arguments.createMap();
                para.putDouble("value", getNormalizationVolume(VOL_MUSIC));
                para.putDouble(VOL_VOICE_CALL, getNormalizationVolume(VOL_VOICE_CALL));
                para.putDouble(VOL_SYSTEM, getNormalizationVolume(VOL_SYSTEM));
                para.putDouble(VOL_RING, getNormalizationVolume(VOL_RING));
                para.putDouble(VOL_MUSIC, getNormalizationVolume(VOL_MUSIC));
                para.putDouble(VOL_ALARM, getNormalizationVolume(VOL_ALARM));
                para.putDouble(VOL_NOTIFICATION, getNormalizationVolume(VOL_NOTIFICATION));
                try {
                    mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("EventVolume", para);
                } catch (RuntimeException e) {
                    // Possible to interact with volume before JS bundle execution is finished.
                    // This is here to avoid app crashing.
                }
            }
        }
    }
}
