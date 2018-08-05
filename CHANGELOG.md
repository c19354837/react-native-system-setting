# V1.5.2
**2018-08-05**

`setVolume(0)` may crash in >= Android M, see [detail](https://github.com/c19354837/react-native-system-setting/pull/48)

# V1.5.1
**2018-08-02**

show System Volume UI by default for iOS, see [detail](https://github.com/c19354837/react-native-system-setting/pull/43)

# V1.5.0
**2018-07-10**

breaking change: remove default Android permission, see [detail](https://github.com/c19354837/react-native-system-setting/pull/44)

# V1.4.6
**2018-06-20**

fix bug: `switchAirplane()` will open Bluetooth Setting Page for iOS.

# V1.4.5
**2018-06-07**

fix bug: Exception handling for Volume Event, see [detail](https://github.com/c19354837/react-native-system-setting/issues/39) 

# V1.4.4
**2018-05-27**

new API: `getLocationMode()`, see [detail](https://github.com/c19354837/react-native-system-setting/issues/35)

fix bug: all listener may get `null` sometime 

# V1.4.3
**2018-05-21**

for iOS - override `+(BOOL)requiresMainQueueSetup` to remove warning

# V1.4.2
**2018-05-07**

new API: `addLocationListener()`, only works in Android

new API: `addAirplaneListener()`, only works in Android

# V1.4.1
**2018-04-23**

new API: `addWifiListener()`, only works in Android

new API: `addBluetoothListener()`, Android & iOS

# V1.4.0
**2018-04-21**

new API: `setAppStore()`, it will avoid some troubles when you submit app to App Store, see [detail](https://github.com/c19354837/react-native-system-setting/issues/28)

# V1.3.0
**2018-04-15**

new API: `switchBluetoothSilence()`, see [detail](https://github.com/c19354837/react-native-system-setting/blob/master/API.md)

**breaking change**: remove `android.permission.CHANGE_WIFI_STATE` from [AndroidManifest.xml](https://github.com/c19354837/react-native-system-setting/blob/master/android/src/main/AndroidManifest.xml). If you want to call `switchWifiSilence()`, you should declare the permission in your Androidmanifest.xml, see [detail](https://github.com/c19354837/react-native-system-setting#powerful-api)

# V1.2.5
**2018-04-07**

fix bug - `setVolume(val, config)` will cause a crash when `type` is null, see [detail](https://github.com/c19354837/react-native-system-setting/issues/22)

fix bug - `grantWriteSettingPremission()` navigates to the wrong page, see [detail](https://github.com/c19354837/react-native-system-setting/issues/24)

# V1.2.4
**2018-03-14**

fix bug - `isLocationEnabled` return `false` when locate by network only, see [detail](https://github.com/c19354837/react-native-system-setting/issues/19)

# V1.2.3
**2018-02-26**

fix bug - `setVolume` will show the `MPVolumeView` in the upper left corner, see [detail](https://github.com/c19354837/react-native-system-setting/issues/17)

# V1.2.2
**2018-02-25**

use `MPVolumeView` to get/set volume for iOS.

change API `setVolume(val, type)` to `setVolume(val, config)`, see [detail](https://github.com/c19354837/react-native-system-setting/issues/15)

# V1.2.1
**2018-01-21**

Supported Cocoapods, see [detail](https://github.com/c19354837/react-native-system-setting/issues/10)

# V1.2.0
**2018-01-07**

new API: `isAirplaneEnabled()` & `switchAirplane(complete)`, see [detail](https://github.com/c19354837/react-native-system-setting/blob/master/API.md)

# V1.1.1
**2018-01-03**

Supported >= iOS 8.0.

new API: `setAppBrightness(val:float)` & `getAppBrightness()`, see [detail](https://github.com/c19354837/react-native-system-setting/blob/master/API.md)

# V1.1.0
**2017-12-14**

Check permission when write setting, see [detail](https://github.com/c19354837/react-native-system-setting#runtime-permission-for-android-6)

# V1.0.8
**2017-11-27**

Now you can change volume by type (Android only), see [detail](https://github.com/c19354837/react-native-system-setting/issues/5)

# V1.0.7
**2017-08-19**

support RN 0.47, see [detail](https://github.com/c19354837/react-native-system-setting/issues/1)

# V1.0.6
**2017-07-23**

support switching bluetooth state

# V1.0.5
**2017-07-15**

support switching location state

API change : `switchWifi()` and `switchWifiSilence()`, see [detail](https://github.com/c19354837/react-native-system-setting/blob/master/API.md)

# V1.0.4
**2017-06-16**

support switching wifi state

# V1.0.3
**2017-06-05**

Save & Restore for brightness

# V1.0.2
**2017-06-03**

Add listener for volume

# V1.0.1
**2017-05-31**

Add screen mode (Android only)

# V1.0.0
**2017-05-30**

Support volume and brightness
