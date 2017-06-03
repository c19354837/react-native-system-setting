package com.ninty.system.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.provider.Settings;

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

    private ReactApplicationContext context;
    private AudioManager am;
    private BroadcastReceiver volumeBR;
    IntentFilter filter;

    public SystemSetting(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
        am = (AudioManager) getReactApplicationContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        listerVolume(reactContext);
    }

    private void listerVolume(final ReactApplicationContext reactContext) {
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

        context.registerReceiver(volumeBR, filter);
    }

    @Override
    public String getName() {
        return "SystemSetting";
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
        context.unregisterReceiver(volumeBR);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (val * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), AudioManager.FLAG_PLAY_SOUND);
        context.registerReceiver(volumeBR, filter);
    }

    @ReactMethod
    public void getVolume(Promise promise) {
        promise.resolve(getNormalizationVolume());
    }

    private float getNormalizationVolume() {
        return am.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0f / am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }
}
