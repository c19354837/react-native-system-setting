## API

**All are static method, and all GET mothods return a promise**

**Maybe [codes](https://github.com/c19354837/react-native-system-setting/blob/master/SystemSetting.js) is best document**

> Some APIs are marked with an asterisk at the beginning, and it means you need declare permission for Androidn since V1.5.0, see [Android Permission](https://github.com/c19354837/react-native-system-setting#android-permission)
> 
> Some APIs which start with `swtich` or containt `bluetooth` are disabled for iOS since V1.7.0, you can [enable it](https://github.com/c19354837/react-native-system-setting/blob/master/iOS.md#ios) in a few steps. 

method | description
------ | -----------
**Volume**|
getVolume(type:string) => Promise | Get the system volume. <br><br>`type` must be one of `music`, `call`, `system`, `ring`, `alarm`, `notification`, default is `music`
setVolume(val:float, config:object) | Set the system volume by specified value, from 0 to 1. 0 for mute, and 1 is max volume.<br><br> `config` can be `{type: 'music', playSound:true, showUI:true}`<br><br> `type` : must be one of `music`, `call`, `system`, `ring`, `alarm`, `notification`, default is `music`.(Android only) <br>`playSound`: Whether to play a sound when changing the volume, default is `false`(Android only)<br>`showUI`: Show a toast containing the current volume, default is `false`(Android & iOS)<br><br> **since 1.2.2**
addVolumeListener(callback) | Listen the volume changing, and it will return the listener. More info see [the example](https://github.com/c19354837/react-native-system-setting/blob/master/examples/SystemSettingExample/index.js#L42)
removeVolumeListener(listener)| Remove listener when it no longer needed.
---|---
**Brightness**|
getBrightness() => Promise | Get the system brightness.
setBrightness(val:float) => Promise | Set the system brightness by specified value, from 0 to 1. 0 for brightless, and 1 is max.<br><br>Return false if permission deny ( iOS always be true
\* setBrightnessForce(val:float) => Promise| In Android, if the screen mode is auto, SystemSetting.setBrightness() will not work. You can call this to change the screen mode to MANUAL first. <br><br>Return false if permission deny ( iOS always be true
\* setAppBrightness(val:float)| For Android, `setBrightness()` or `setBrightnessForce()` will change the system's brightness, while this just changes the app's brightness, and it has no permission trouble.<br><br> For iOS, it's same with `setBrightness()`.
getAppBrightness() => Promise | Get the app brightness, and it will returns system brightness if you haven't call `setAppBrightness(val)` yet. (iOS allways returns system brightness)
getScreenMode() => Promise| (Only for Android, iOS will return -1). Get the screen mode, 0 is manual, while 1 is automatic.
\* setScreenMode(mode:int) => Promise|(Only for Android, iOS cannot change it). Change the screen mode, 0 is manual, while 1 is automatic.<br><br>Return false if permission deny ( iOS always be true
grantWriteSettingPermission()| open app setting page. It's user-friendly when you need some permission. Normally, you can call it if `setScreenMode()`, `setBrightness()` or `setBrightnessForce()` return false 
\* saveBrightness()|It will save current brightness and screen mode.
restoreBrightness() => Promise|Restore brightness and screen mode back to saveBrightness(). While iOS only restore the brightness, Android will restore both. <br><br>You should call this before setBrightness() or setBrightnessForce(). <br><br>It will return the saved brightness.
---|---
**Wifi**|
\* isWifiEnabled() => Promise|Get wifi state, true if wifi is on.
switchWifi(complete)|It will open **Wifi Setting Page**, and you can change it by yourself. When come back to the app, the `complete` will be call.
\*\* switchWifiSilence(complete)|It will open wifi if the wifi is off, and close wifi when the wifi is on now. When it has done, the `complete` will be call.<br/>In android, it's done programmatically. <br><br>In iOS, I cannot do that by code for system limiting, so it just calls `switchWifi(complete)`<br><br>You need declare `android.permission.CHANGE_WIFI_STATE` in your AndroidManifest.xml, see [detail](https://github.com/c19354837/react-native-system-setting#android-permission)
addWifiListener(callback) => Promise| Listen the wifi state changing, and it will return the listener. (Android only)
---|---
**Location**|
isLocationEnabled() => Promise|Get location state, true if location is on.
switchLocation(complete)|It will open **System Location Setting Page**, and you can change it by yourself. When come back to the app, the `complete` will be call.
addLocationListener(callback) => Promise| Listen the location state changing, and it will return the listener. (Android only)
getLocationMode() => Promise| Get current location mode code: `0` - 'off', `1` - 'gps', `2` - 'network', `3` - 'gps & network'. (Android only)
addLocationModeListener(callback) => Promise| Listen the location mode changing, and it will return the listener. (Android only)
---|---
**Bluetooth**|
\* isBluetoothEnabled() => Promise|Get bluetooth state, true if bluetooth is on.
switchBluetooth(complete)|It will open **System Bluetooth Setting Page**, and you can change it by yourself. When come back to the app, the `complete` will be call.
\*\* switchBluetoothSilence(complete)|It will open bluetooth if the bluetooth is off, and close bluetooth when the bluetooth is on now. When it has done, the `complete` will be call.<br/>In android, it's done programmatically. <br><br>In iOS, I cannot do that by code for system limiting, so it just calls `switchBluetooth(complete)`<br><br>You need declare `android.permission.BLUETOOTH_ADMIN` in your AndroidManifest.xml, see [detail](https://github.com/c19354837/react-native-system-setting#android-permission)
addBluetoothListener(callback) => Promise| Listen the bluetooth state changing, and it will return the listener.
---|---
**Airplane**|
isAirplaneEnabled() => Promise|Get airplane state, true if airplane is on. <br><br>It will always return `true` for iOS if your device has no SIM card, see [detail](https://github.com/c19354837/react-native-system-setting/issues/37)
switchAirplane(complete)|It will open **System Setting Page**, and you can change it by yourself. When come back to the app, the `complete` will be call.
addAirplaneListener(callback) => Promise| Listen the airplane state changing, and it will return the listener. (Android only)
---|---
**Other**|
setAppStore(isAppStore:bool)| `true` means that you'll submit your app to App Store. In order to throught the App Store review, it has some side effects, see [Private API](https://github.com/c19354837/react-native-system-setting/blob/master/iOS.md#private-api).<br><br>`false` means that your app will not upload to App Store, and you can use any APIs at will.
removeListener(listener)| you can use this to remove the listener which return by `add*Listener(callback)`
openAppSystemSettings()| open app's setting page