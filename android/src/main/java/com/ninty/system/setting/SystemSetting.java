package com.ninty.system.setting;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by ninty on 2017/5/29.
 */

public class SystemSetting extends ReactContextBaseJavaModule{

    private AudioManager am ;

    public SystemSetting(ReactApplicationContext reactContext) {
        super(reactContext);
        am = (AudioManager) getReactApplicationContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public String getName() {
        return "SystemSetting";
    }

    @ReactMethod
    public void setScreenMode(int mode){
        mode = mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL ? mode : Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        Settings.System.putInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }

    @ReactMethod
    public void getScreenMode(Promise promise){
        try {
            int mode = Settings.System.getInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            promise.resolve(mode);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            promise.reject("-1","get screen mode fail", e);
        }
    }

    @ReactMethod
    public void setBrightness(float val){
//        setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        final int brightness = (int)(val * 255);
        Settings.System.putInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    @ReactMethod
    public void getBrightness(Promise promise){
        try {
            int val = Settings.System.getInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            promise.resolve(val * 1.0f / 255);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            promise.reject("-1","get brightness fail", e);
        }
    }

    @ReactMethod
    public void setVolume(float val){
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (val * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), AudioManager.FLAG_PLAY_SOUND);
    }

    @ReactMethod
    public  void getVolume(Promise promise){
        float volume = am.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0f / am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        promise.resolve(volume);
    }
}
