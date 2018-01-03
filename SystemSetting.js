import {NativeModules, NativeEventEmitter} from 'react-native'

import Utils from './Utils'

const SystemSettingNative = NativeModules.SystemSetting

const SCREEN_BRIGHTNESS_MODE_UNKNOW = -1
const SCREEN_BRIGHTNESS_MODE_MANUAL = 0
const SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1

const eventEmitter = new NativeEventEmitter(SystemSettingNative)

export default class SystemSetting {
    static saveBrightnessVal = -1
    static saveScreenModeVal = SCREEN_BRIGHTNESS_MODE_AUTOMATIC

    static async getBrightness() {
        return await SystemSettingNative.getBrightness()
    }

    static async setBrightness(val) {
		try{
			await SystemSettingNative.setBrightness(val)
			return true
		}catch(e){
			return false
		}
    }

    static async setBrightnessForce(val) {
        if (Utils.isAndroid) {
			const success = await SystemSetting.setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL)
			if(!success){
				return false
			}
		}
		return await SystemSetting.setBrightness(val)
	}

    static setAppBrightness(val) {
        if (Utils.isAndroid) {
			SystemSettingNative.setAppBrightness(val)
		}else{
			SystemSetting.setBrightness(val)
		}
		return true
	}
	
	static grantWriteSettingPremission(){
		if (Utils.isAndroid) {
            SystemSettingNative.openWriteSetting()
		}
	}

    static async getScreenMode() {
        if (Utils.isAndroid) {
            return await SystemSettingNative.getScreenMode()
        }
        return -1 // cannot get iOS screen mode
    }

    static async setScreenMode(val) {
        if (Utils.isAndroid) {
			try{
				await SystemSettingNative.setScreenMode(val)
			}catch(e){
				return false
			}
		}
		return true
    }

    static async saveBrightness(){
        SystemSetting.saveBrightnessVal = await SystemSetting.getBrightness()
        SystemSetting.saveScreenModeVal = await SystemSetting.getScreenMode()
    }

    static restoreBrightness(){
        if(SystemSetting.saveBrightnessVal == -1){
            console.warn('you should call saveBrightness() at least once')
        }else{
            SystemSetting.setBrightness(SystemSetting.saveBrightnessVal)
            SystemSetting.setScreenMode(SystemSetting.saveScreenModeVal)
        }
        return SystemSetting.saveBrightnessVal
    }

    static async getVolume(type='music') {
        return await SystemSettingNative.getVolume(type)
    }

    static setVolume(val, type='music') {
        SystemSettingNative.setVolume(val, type)
    }

    static addVolumeListener(callback) {
        return eventEmitter.addListener('EventVolume', callback)
    }

    static removeVolumeListener(listener){
        listener && listener.remove()
    }

    static async isWifiEnabled(){
        const result = await SystemSettingNative.isWifiEnabled()
        return (result) > 0
    }

    static switchWifiSilence(complete){
        if(Utils.isAndroid){
            SystemSettingNative.switchWifiSilence()
            SystemSetting.listenEvent(complete, 'EventWifiChange')
        }else{
            SystemSetting.switchWifi(complete)
        }
    }

    static switchWifi(complete){
        SystemSettingNative.switchWifi()
        SystemSetting.listenEvent(complete, 'EventWifiChange')
    }

    static async isLocationEnabled(){
        return await SystemSettingNative.isLocationEnabled()
    }

    static switchLocation(complete){
        SystemSettingNative.switchLocation()
        SystemSetting.listenEvent(complete, 'EventLocationChange')
    }

    static async isBluetoothEnabled(){
        return await SystemSettingNative.isBluetoothEnabled()
    }

    static switchBluetooth(complete){
        SystemSettingNative.switchBluetooth()
        SystemSetting.listenEvent(complete, 'EventBluetoothChange')
    }

    static listenEvent(complete, androidEvent){
        const listener = eventEmitter.addListener(Utils.isAndroid ? androidEvent : 'EventEnterForeground', () => {
            listener.remove()
            complete()
        })
    }
}