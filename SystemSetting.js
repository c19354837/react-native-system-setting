import {NativeModules, NativeEventEmitter} from 'react-native'

import Utils from './Utils'

const SystemSettingNative = NativeModules.SystemSetting

const SCREEN_BRIGHTNESS_MODE_MANUAL = 0
const SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1

const volumeEmitter = new NativeEventEmitter(SystemSettingNative);

export default class SystemSetting {
    static async getBrightness() {
        return await SystemSettingNative.getBrightness()
    }

    static setBrightness(val) {
        SystemSettingNative.setBrightness(val)
    }

    static setBrightnessForce(val) {
        if (Utils.isAndroid) {
            SystemSetting.setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL)
        }
        SystemSettingNative.setBrightness(val)
    }

    static async getScreenMode() {
        if (Utils.isAndroid) {
            return await SystemSettingNative.getScreenMode()
        }
        return -1 // cannot get iOS screen mode
    }

    static setScreenMode(val) {
        if (Utils.isAndroid) {
            SystemSettingNative.setScreenMode(val)
        }
    }

    static async getVolume() {
        return await SystemSettingNative.getVolume()
    }

    static setVolume(val) {
        SystemSettingNative.setVolume(val)
    }

    static addVolumeListener(callback) {
        return volumeEmitter.addListener('EventVolume', callback)
    }

    static removeVolumeListener(listener){
        listener && listener.remove()
    }

}
