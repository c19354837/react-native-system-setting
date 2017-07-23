package com.ninty.system.setting;

import android.provider.Settings;

/**
 * Created by ninty on 2017/7/2.
 */

public enum SysSettings {

    UNKNOW("", 0, ""),
    WIFI(Settings.ACTION_WIFI_SETTINGS, 1, "EventWifiChange"),
    LOCATION(Settings.ACTION_LOCATION_SOURCE_SETTINGS, 2, "EventLocationChange"),
    BLUETOOTH(Settings.ACTION_BLUETOOTH_SETTINGS, 3, "EventBluetoothChange");

    public String action;
    public int requestCode;
    public String event;

    SysSettings(String action, int requestCode, String event) {
        this.action = action;
        this.requestCode = requestCode;
        this.event = event;
    }

    public static SysSettings get(int requestCode){
        for (SysSettings setting : SysSettings.values()) {
            if(setting.requestCode == requestCode){
                return setting;
            }
        }
        return UNKNOW;
    }
}
