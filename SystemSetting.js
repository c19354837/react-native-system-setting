import {NativeModules, Platform} from 'react-native'

const SystemSettingNative = NativeModules.SystemSetting

const SCREEN_BRIGHTNESS_MODE_MANUAL = 0
const SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1

export default class SystemSetting {
    static async getBrightness() {
        return await SystemSettingNative.getBrightness()
    }

    static setBrightness(val) {
        SystemSettingNative.setBrightness(val)
    }

    static setBrightnessForce(val) {
        if(Platform.OS === 'android'){
            SystemSetting.setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL)
        }
        SystemSettingNative.setBrightness(val)
    }

    static async getScreenMode(){
        return await SystemSettingNative.getBrightness()
    }

    static setScreenMode(val){
        SystemSettingNative.setScreenMode(val)
    }

    static async getVolume() {
        return await SystemSettingNative.getVolume()
    }

    static setVolume(val) {
        SystemSettingNative.setVolume(val)
    }
}
