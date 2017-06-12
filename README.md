## react-native-system-setting
It provides some system setting APIs for you. Support iOS and Android both.

### Support

* Volume ( with listener)
* Brightness

### Next

* Add more settings
* Support mobx

**Example only work in the real device**

## Look like

I really want to show the .gif, while it has no diffrence with .jpg for some system limit.

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

If link does not work, you can do it manually. Just follow this way:

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

// listen the volume changing if you need
const volumeListener = SystemSetting.addVolumeListener((data) => {
    const volume = data.value;
    console.log(volume);
});

//remove listener when you need no more
SystemSetting.removeVolumeListener(volumeListener)       
```

**brightness**

```javascript
//get the current brightness
const currentBrightness = SystemSetting.getBrightness();

//change the brightness
SystemSetting.setBrightnessForce(0.5);

// save the value of  brightness and screen mode.
SystemSetting.saveBrightness();
// restore the brightness and screen mode. you can get the old brightness value.
const brightnessVal = SystemSetting.restoreBrightness()
```

## API

**All are static method**

`getVolume()`

Get the system volume.

`setVolume(val:float)`

Set the system volume by specified value, from 0 to 1. 0 for mute, and 1 is max volume.

`addVolumeListener(callback)`

Listen the volume changing, and it will return the listener. More info see [the example](https://github.com/c19354837/react-native-system-setting/blob/master/examples/SystemSettingExample/index.js#L28)

`removeVolumeListener(listener)`

Remove listener when it no longer needed.

`getBrightness()`

Get the system brightness.

`setBrightness(val:float)`

Set the system brightness by specified value, from 0 to 1. 0 for brightless, and 1 is max.

`setBrightnessForce(val:float)`

In Android, if the screen mode is auto, SystemSetting.setBrightness() will not work.
You can call this to change the screen mode to MANUAL first.

`getScreeenMode()`

(Only for Android, iOS will return -1). Get the screen mode, 0 is manual, while 1 is automatic.

`setScreeenMode(mode:int)`

(Only for Android, iOS cannot change it). Change the screen mode, 0 is manual, while 1 is automatic.

`saveBrightness()`

It will save current brightness and screen mode.

`restoreBrightness()`

Restore brightness and screen mode back to saveBrightness(). While iOS only restore the brightness, Android will restore both. You should call this before setBrightness() or setBrightnessForce(). It will return the saved brightness.

## Run example

```
cd example/SystemSettingExample
npm install
```

## In the end

Feel free to open issue or pull request
