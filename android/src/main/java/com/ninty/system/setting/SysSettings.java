package com.ninty.system.setting;

import android.provider.Settings;

/**
 * Created by ninty on 2017/7/2.
 */

public enum SysSettings {

    UNKNOW("", 0),
    WIFI(Settings.ACTION_WIFI_SETTINGS, 1),
    LOCATION(Settings.ACTION_LOCATION_SOURCE_SETTINGS, 2),
    BLUETOOTH(Settings.ACTION_BLUETOOTH_SETTINGS, 3),
    WRITESETTINGS(Settings.ACTION_MANAGE_WRITE_SETTINGS, 4),
    AIRPLANE(Settings.ACTION_AIRPLANE_MODE_SETTINGS, 5);

    public String action;
    public int requestCode;

    SysSettings(String action, int requestCode) {
        this.action = action;
        this.requestCode = requestCode;
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
