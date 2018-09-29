import React, { Component } from 'react'
import { StyleSheet, Alert, Text, View, Slider, TouchableOpacity, Switch, ActivityIndicator, ScrollView, Platform } from 'react-native'

import SystemSetting from 'react-native-system-setting'

export default class SystemSettingExample extends Component {

    isAndroid = Platform.OS === 'android'

    volumeListener = null
    wifiListener = null
    bluetoothListener = null
    locationListener = null
    airplaneListener = null

    volTypes = ['music', 'system', 'call', 'ring', 'alarm', 'notification']
    volIndex = 0

    constructor(props) {
        super(props)
        this.state = {
            volume: 0,
            volType: this.volTypes[this.volIndex],
            brightness: 0,
            wifiEnable: false,
            wifiStateLoading: false,
            locationEnable: false,
            locationStateLoading: false,
            bluetoothEnable: false,
            bluetoothStateLoading: false,
            airplaneEnable: false,
            airplaneStateLoading: false,
        }
    }

    async componentDidMount() {
        this.setState({
            volume: await SystemSetting.getVolume(this.state.volType),
            brightness: await SystemSetting.getBrightness(),
            wifiEnable: await SystemSetting.isWifiEnabled(),
            locationEnable: await SystemSetting.isLocationEnabled(),
            bluetoothEnable: await SystemSetting.isBluetoothEnabled(),
            airplaneEnable: await SystemSetting.isAirplaneEnabled(),
        })
        // just init slider value directly
        this._changeSliderNativeVol(this.sliderVol, this.state.volume)
        this._changeSliderNativeVol(this.sliderBri, this.state.brightness)

        this.volumeListener = SystemSetting.addVolumeListener((data) => {
            const volume = this.isAndroid ? data[this.state.volType] : data.value
            this._changeSliderNativeVol(this.sliderVol, volume)
            this.setState({
                volume: volume
            })
        })

        this.wifiListener = await SystemSetting.addWifiListener((enable) => {
            this.setState({ wifiEnable: enable })
        })

        this.bluetoothListener = await SystemSetting.addBluetoothListener((enable) => {
            this.setState({ bluetoothEnable: enable })
        })

        this.locationListener = await SystemSetting.addLocationListener((enable) => {
            this.setState({ locationEnable: enable })
        })

        this.airplaneListener = await SystemSetting.addAirplaneListener((enable) => {
            this.setState({ airplaneEnable: enable })
        })
    }

    _changeSliderNativeVol(slider, value) {
        slider.setNativeProps({
            value: value
        })
    }

    componentWillUnmount() {
        SystemSetting.removeListener(this.volumeListener)
        SystemSetting.removeListener(this.wifiListener)
        SystemSetting.removeListener(this.bluetoothListener)
        SystemSetting.removeListener(this.locationListener)
        SystemSetting.removeListener(this.airplaneListener)
    }

    _changeVol(value) {
        SystemSetting.setVolume(value, {
            type: this.state.volType,
            playSound: false,
            showUI: false
        })
        this.setState({
            volume: value
        })
    }

    _changeVolType = async () => {
        this.volIndex = ++this.volIndex % this.volTypes.length
        const volType = this.volTypes[this.volIndex]
        const vol = await SystemSetting.getVolume(volType)
        this._changeSliderNativeVol(this.sliderVol, vol)
        this.setState({
            volType: volType,
            volume: vol
        })
    }

    _changeBrightness = async (value) => {
        const result = await SystemSetting.setBrightnessForce(value)
        if (!result) {
            Alert.alert('Permission Deny', 'You have no permission changing settings', [
                { 'text': 'Ok', style: 'cancel' },
                { 'text': 'Open Setting', onPress: () => SystemSetting.grantWriteSettingPremission() }
            ])
            return
        }
        this.setState({
            brightness: value,
        })
    }

    _restoreBrightness() {
        const saveBrightnessVal = SystemSetting.restoreBrightness()
        if (saveBrightnessVal > -1) {
            // success
            this.setState({
                brightness: saveBrightnessVal
            })
            this._changeSliderNativeVol(this.sliderBri, saveBrightnessVal)
        }
    }

    _switchWifi() {
        this.setState({
            wifiStateLoading: true
        })
        SystemSetting.switchWifi(async () => {
            this.setState({
                wifiEnable: await SystemSetting.isWifiEnabled(),
                wifiStateLoading: false
            })
        })
    }

    _switchLocation() {
        this.setState({
            locationStateLoading: true
        })
        SystemSetting.switchLocation(async () => {
            this.setState({
                locationEnable: await SystemSetting.isLocationEnabled(),
                locationStateLoading: false
            })
        })
    }

    _switchBluetooth() {
        this.setState({
            bluetoothStateLoading: true
        })
        SystemSetting.switchBluetooth(async () => {
            this.setState({
                bluetoothEnable: await SystemSetting.isBluetoothEnabled(),
                bluetoothStateLoading: false
            })
        })
    }

    _switchAirplane() {
        this.setState({
            airplaneStateLoading: true
        })
        SystemSetting.switchAirplane(async () => {
            this.setState({
                airplaneEnable: await SystemSetting.isAirplaneEnabled(),
                airplaneStateLoading: false
            })
        })
    }

    render() {
        const { volume, brightness,
            wifiEnable, wifiStateLoading,
            locationEnable, locationStateLoading,
            bluetoothEnable, bluetoothStateLoading,
            airplaneEnable, airplaneStateLoading,
        } = this.state
        return (
            <ScrollView style={styles.container}>
                <View style={styles.head}>
                </View>
                <ValueView
                    title='Volume'
                    btn={this.isAndroid && {
                        title: this.state.volType,
                        onPress: this._changeVolType
                    }}
                    value={volume}
                    changeVal={(val) => this._changeVol(val)}
                    refFunc={(sliderVol) => this.sliderVol = sliderVol}
                />
                <ValueView
                    title='Brightness'
                    value={brightness}
                    changeVal={(val) => this._changeBrightness(val)}
                    refFunc={(sliderBri) => this.sliderBri = sliderBri}
                />
                <View style={styles.card}>
                    <View style={styles.row}>
                        <Text style={styles.title}>Brightness save & restore
                        </Text>
                    </View>
                    <View style={styles.row}>
                        <TouchableOpacity style={{ marginRight: 32 }} onPress={SystemSetting.saveBrightness}>
                            <Text style={styles.btn}>Save
                            </Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={this._restoreBrightness.bind(this)}>
                            <Text style={styles.btn}>Restore
                            </Text>
                        </TouchableOpacity>
                    </View>
                </View>
                <StatusView
                    title='Wifi'
                    value={wifiEnable}
                    loading={wifiStateLoading}
                    switchFunc={(value) => this._switchWifi()} />
                <StatusView
                    title='Location'
                    value={locationEnable}
                    loading={locationStateLoading}
                    switchFunc={(value) => this._switchLocation()} />
                <StatusView
                    title='Bluetooth'
                    value={bluetoothEnable}
                    loading={bluetoothStateLoading}
                    switchFunc={(value) => this._switchBluetooth()} />
                <StatusView
                    title='Airplane'
                    value={airplaneEnable}
                    loading={airplaneStateLoading}
                    switchFunc={(value) => this._switchAirplane()} />
            </ScrollView>
        )
    }
}

const ValueView = (props) => {
    const { title, value, changeVal, refFunc, btn } = props
    return (
        <View style={styles.card}>
            <View style={styles.row}>
                <Text style={styles.title}>{title}</Text>
                {btn && <TouchableOpacity onPress={btn.onPress}>
                    <Text style={styles.btn}>{btn.title}
                    </Text>
                </TouchableOpacity>}
                <Text style={styles.value}>{value}</Text>
            </View>
            <Slider
                ref={refFunc}
                style={styles.slider}
                onValueChange={changeVal} />
        </View>
    )
}

const StatusView = (props) => {
    const { title, switchFunc, value, loading } = props
    return (
        <View style={styles.card}>
            <View style={styles.row}>
                <Text style={styles.title}>{title}
                </Text>
            </View>
            <View style={styles.row}>
                <Text>Current status is {loading ? 'switching' : (value ? 'On' : 'Off')}
                </Text>
                {
                    loading && <ActivityIndicator animating={loading} />
                }
                <View style={{ flex: 1, alignItems: 'flex-end' }}>
                    <Switch
                        onValueChange={switchFunc}
                        value={value} />
                </View>
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#E5E7E8'
    },
    head: {
        justifyContent: 'center',
        alignItems: 'center',
        height: 64,
    },
    headline: {
        fontSize: 22,
        color: '#666'
    },
    card: {
        padding: 8,
        backgroundColor: '#fff',
        marginTop: 4,
        marginBottom: 4,
    },
    row: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingTop: 8,
        paddingBottom: 8,
    },
    title: {
        fontSize: 16,
        color: '#6F6F6F'
    },
    value: {
        fontSize: 14,
        flex: 1,
        textAlign: 'right',
        color: '#904ED9'
    },
    split: {
        marginVertical: 16,
        height: 1,
        backgroundColor: '#ccc',
    },
    btn: {
        fontSize: 16,
        padding: 8,
        paddingVertical: 8,
        color: '#405EFF'
    }
})