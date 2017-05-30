## react-native-system-setting
It provides some system setting APIs for you. Support iOS and Android both.

### Support
* Volume
* Brightness

### In the future
* All the system setting

**Example only work in the real device**

## Look like

I really want to show the .gif, while it has no diffrence with .jpg. 

I strongly recommend you to run the example in real device to see how it works.

<img src="https://raw.githubusercontent.com/c19354837/react-native-system-setting/master/screenshot/ios.png" width = "40%"/>&nbsp;&nbsp;&nbsp;
<img src="https://raw.githubusercontent.com/c19354837/react-native-system-setting/master/screenshot/android.jpg" width = "40%" />

## Install
Run `npm i -S react-native-system-setting`

#### iOS
Run `react-native link` to link this library.

#### Android
Run `react-native link` to link this library.

That's all.

If link does not work, or you just want to do it manually, you can follow this way:

**android/settings.gradle**

```gradle
include ':react-native-system-setting'
project(':react-native-system-setting').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-system-setting/android')
```

**android/app/build.gradle**

```gradle
dependencies {
   ...
   compile project(':react-native-system-setting')
}
```

**MainApplication.java**

On top, where imports are:

```java
import com.ninty.react.ReactSystemSetting;
```

Add the `ReactSystemSettingPackage` class to your list of exported packages.

```java
@Override
protected List<ReactPackage> getPackages() {
    return Arrays.asList(
            new MainReactPackage(),
            new ReactSystemSettingPackage()
    );
}
```


## Usage

**Common import**

```javascript
import SystemSetting from 'react-native-system-setting'
```

**volume**

```javascript
//get the current volume
const currentVol = SystemSetting.getVolume();

// change the volume
SystemSetting.setVolume(0.5);
```

**brightness**

```javascript
//get the current brightness
const currentBrightness = SystemSetting.getBrightness();

// note:In Android, if the screen mode is auto, SystemSetting.setBrightness() will not work.
// You should call SystemSetting.switchScreenModeToManual() before change the system brightness.

SystemSetting.switchScreenModeToManual();// Android need call it before setBrightness

//change the brightness
SystemSetting.setBrightness(0.5);
```

## API

All are static method

`getVolume()`

Get the system volume.

`setVolume(float)`

Set the system volume by specified value, from 0 to 1. 0 for mute, and 1 is max volume.

`getBrightness()`

Get the system brightness.

`setBrightness(float)`

Set the system brightness by specified value, from 0 to 1. 0 for brightless, and 1 is max.

## In the end

Feel free to open issue or pull request