package com.ninty.system.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by ninty on 2017/5/29.
 */

public class SystemSetting extends ReactContextBaseJavaModule {

    private ReactApplicationContext mContext;
    private AudioManager am;
    private WifiManager wm;
    private BroadcastReceiver volumeBR;
    private volatile BroadcastReceiver wifiBR;
    private IntentFilter filter;
    private String TAG = SystemSetting.class.getSimpleName();

    public SystemSetting(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        am = (AudioManager) getReactApplicationContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        wm = (WifiManager) getReactApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        listenVolume(reactContext);
    }

    private void listenVolume(final ReactApplicationContext reactContext) {
        volumeBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                    WritableMap para = new Arguments().createMap();
                    para.putDouble("value", getNormalizationVolume());
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("EventVolume", para);
                }
            }
        };
        filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");

        reactContext.registerReceiver(volumeBR, filter);
    }

    private void listenWifiState() {
        if(wifiBR == null){
            synchronized (this){
                if(wifiBR == null){
                    wifiBR = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                                if(wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_DISABLED){
                                    mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                            .emit("EventWifiChange", null);
                                }
                            }
                        }
                    };
                    IntentFilter wifiFilter = new IntentFilter();
                    wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                    wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                    wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

                    mContext.registerReceiver(wifiBR, wifiFilter);
                }
            }
        }
    }

    @Override
    public String getName() {
        return SystemSetting.class.getSimpleName();
    }

    @ReactMethod
    public void setScreenMode(int mode) {
        mode = mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL ? mode : Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        Settings.System.putInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }

    @ReactMethod
    public void getScreenMode(Promise promise) {
        try {
            int mode = Settings.System.getInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            promise.resolve(mode);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            promise.reject("-1", "get screen mode fail", e);
        }
    }

    @ReactMethod
    public void setBrightness(float val) {
        final int brightness = (int) (val * 255);
        Settings.System.putInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    @ReactMethod
    public void getBrightness(Promise promise) {
        try {
            int val = Settings.System.getInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            promise.resolve(val * 1.0f / 255);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            promise.reject("-1", "get brightness fail", e);
        }
    }

    @ReactMethod
    public void setVolume(float val) {
        mContext.unregisterReceiver(volumeBR);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (val * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), AudioManager.FLAG_PLAY_SOUND);
        mContext.registerReceiver(volumeBR, filter);
    }

    @ReactMethod
    public void getVolume(Promise promise) {
        promise.resolve(getNormalizationVolume());
    }

    private float getNormalizationVolume() {
        return am.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0f / am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @ReactMethod
    public void isWifiEnabled(Promise promise){
        if(wm != null){
            promise.resolve(wm.isWifiEnabled());
        }else {
            promise.reject("-1", "get wifi manager fail");
        }
    }

    @ReactMethod
    public void switchWifi(){
        if(wm != null){
            listenWifiState();
            wm.setWifiEnabled(!wm.isWifiEnabled());
        }else {
            Log.w(TAG, "Cannot get wm, switchWifi will be ignored");
        }
    }
}
