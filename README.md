## react-native-system-setting
It provides some system setting APIs for you. Support iOS and Android both.

### Support

* Volume ( with listener)
* Brightness
* Wifi switch
* Location

### Next

* Bluetooth
* Requesting permissions at Run Time (for Android 6+)
* System info

### Note

**Example only work in the real device**

### Change Log
[Change Log](https://github.com/c19354837/react-native-system-setting/blob/master/CHANGELOG.md)

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
SystemSetting.getVolume().then((volume)=>{
    console.log('Current volume is ' + volume);
});

// change the volume
SystemSetting.setVolume(0.5);

// listen the volume changing if you need
const volumeListener = SystemSetting.addVolumeListener((data) => {
    const volume = data.value;
    console.log(volume);
});

//remove listener when you need it no more
SystemSetting.removeVolumeListener(volumeListener)       
```

**brightness**

```javascript
//get the current brightness
SystemSetting.getBrightness().then((brightness)=>{
    console.log('Current brightness is ' + brightness);
});

//change the brightness
SystemSetting.setBrightnessForce(0.5);

// save the value of  brightness and screen mode.
SystemSetting.saveBrightness();
// restore the brightness and screen mode. you can get the old brightness value.
SystemSetting.restoreBrightness().then((oldVal)=>{
    //if you need
})
```

**Wifi**

```javascript
SystemSetting.isWifiEnabled().then((enable)=>{
    const state = enable ? 'On' : 'Off';
    console.log('Current wifi is ' + state);
})

SystemSetting.switchWifi(()=>{
    console.log('switch wifi successfully');
})
```

## API

[API](https://github.com/c19354837/react-native-system-setting/wiki/API)

## Run example

```
$ cd example/SystemSettingExample
$ npm install
// if android
$ react-native run-android
// else
$ react-native run-ios
```

## In the end

Feel free to open issue or pull request

## License
[**MIT**](https://github.com/c19354837/react-native-system-setting/blob/master/LICENSE.md)
