export {};

interface EmitterSubscription {
  remove: () => void;
}

type VolumeType =
  | "call"
  | "system"
  | "ring"
  | "music"
  | "alarm"
  | "notification";

interface VolumeConfig {
  type?: VolumeType;
  playSound?: boolean;
  showUI?: boolean;
}

interface VolumeData {
  value: number;
  call?: number;
  system?: number;
  ring?: number;
  music?: number;
  alarm?: number;
  notification?: number;
}

interface SystemSetting {
  getBrightness: () => Promise<number>;
  setBrightness: (val: number) => Promise<boolean>;
  setBrightnessForce: (val: number) => Promise<boolean>;
  getAppBrightness: () => Promise<number>;
  setAppBrightness: (val: number) => Promise<true>;
  grantWriteSettingPremission: () => void;
  getScreenMode: () => Promise<number>;
  setScreenMode: (val: number) => Promise<boolean>;
  saveBrightness: () => Promise<void>;
  restoreBrightness: () => number;
  getVolume: (type?: VolumeType) => Promise<number>;
  setVolume: (value: number, config?: VolumeConfig | VolumeType) => void;
  addVolumeListener: (
    callback: (volumeData: VolumeData) => void
  ) => EmitterSubscription;
  removeVolumeListener: (listener?: EmitterSubscription) => void;
  isWifiEnabled: () => Promise<boolean>;
  switchWifiSilence: () => void;
  switchWifi: () => void;
  isLocationEnabled: () => Promise<boolean>;
  getLocationMode: () => Promise<number>;
  switchLocation: (callback: () => void) => void;
  isBluetoothEnabled: () => Promise<boolean>;
  switchBluetooth: () => void;
  switchBluetoothSilence: () => void;
  isAirplaneEnabled: () => Promise<boolean>;
  switchAirplane: () => void;
  openAppSystemSettings: () => Promise<void>;
  addBluetoothListener: (
    callback: (bluetoothEnabled: boolean) => void
  ) => Promise<EmitterSubscription>;
  addWifiListener: (
    callback: (wifiEnabled: boolean) => void
  ) => Promise<EmitterSubscription | null>;
  addLocationListener: (
    callback: (locationEnabled: boolean) => void
  ) => Promise<EmitterSubscription | null>;
  addLocationModeListener: (
    callback: (locationMode: number) => void
  ) => Promise<EmitterSubscription | null>;
  addAirplaneListener: (
    callback: (airplaneModeEnabled: boolean) => void
  ) => Promise<EmitterSubscription | null>;
  removeListener: (listener?: EmitterSubscription) => void;
}

declare const systemSetting: SystemSetting;
export default systemSetting;
