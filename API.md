## API

**All are static method, and all GET mothods return a promise**

method | description
------ | -----------
**Volume**|
getVolume(type:string) => Promise | Get the system volume. <br><br>`type` must be one of `'music', 'call', 'system', 'ring', 'alarm', 'notification'`, default is `music`
setVolume(val:float, type:string) | Set the system volume by specified value, from 0 to 1. 0 for mute, and 1 is max volume.<br><br>`type` must be one of `'music', 'call', 'system', 'ring', 'alarm', 'notification'`, default is `music`
addVolumeListener(callback) | Listen the volume changing, and it will return the listener. More info see [the example](https://github.com/c19354837/react-native-system-setting/blob/master/examples/SystemSettingExample/index.js#L42)
removeVolumeListener(listener)| Remove listener when it no longer needed.
---|---
**Brightness**|
getBrightness() => Promise | Get the system brightness.
setBrightness(val:float) => Promise | Set the system brightness by specified value, from 0 to 1. 0 for brightless, and 1 is max.<br><br>Return false if permission deny ( iOS always be true
setBrightnessForce(val:float) => Promise| In Android, if the screen mode is auto, SystemSetting.setBrightness() will not work. You can call this to change the screen mode to MANUAL first. <br><br>Return false if permission deny ( iOS always be true
getScreeenMode() => Promise| (Only for Android, iOS will return -1). Get the screen mode, 0 is manual, while 1 is automatic.
setScreeenMode(mode:int) => Promise|(Only for Android, iOS cannot change it). Change the screen mode, 0 is manual, while 1 is automatic.<br><br>Return false if permission deny ( iOS always be true
grantWriteSettingPremission()| open app setting page. It's user-friendly when you need some permission. Normally, you can call it if `setScreenMode()`, `setBrightness()` or `setBrightnessForce()` return false 
saveBrightness()|It will save current brightness and screen mode.
restoreBrightness() => Promise|Restore brightness and screen mode back to saveBrightness(). While iOS only restore the brightness, Android will restore both. <br><br>You should call this before setBrightness() or setBrightnessForce(). <br><br>It will return the saved brightness.
---|---
**Wifi**|
isWifiEnabled():Promise|Get wifi state, true if wifi is on.
switchWifi(complete)|It will open wifi if the wifi is off, and close it when it's on now. When it has done, the `complete` will be call.
switchWifiSilence(complete)|It will open wifi if the wifi is off, and close wifi when the wifi is on now. When it has done, the `complete` will be call.<br/>In android, it's done programmatically. <br><br>In iOS, I cannot do that by code for system limiting, so it just calls `switchWifi(complete)`
switchWifi(complete)|It's a little different from `switchWifiSilence()`. It will open **System Wifi Settings Page**, and you can open the wifi by yourself.
---|---
**Location**|
isLocationEnabled():Promise|Get location state, true if location is on.
switchLocation(complete)|It will open **System Location Setting Page**, and you can change it by yourself. When come back to the app, the `complete` will be call.
---|---
**Bluetooth**|
isBluetoothEnabled():Promise|Get bluetooth state, true if bluetooth is on.
switchBluetooth(complete)|It will open **System Bluetooth Setting Page**, and you can change it by yourself. When come back to the app, the `complete` will be call.


