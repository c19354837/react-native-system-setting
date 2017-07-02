import React, {Component} from 'react';
import {AppRegistry, StyleSheet, Text, View, Slider, TouchableOpacity, PixelRatio, Switch, ActivityIndicator, ScrollView} from 'react-native';

import SystemSetting from 'react-native-system-setting'

export default class SystemSettingExample extends Component {

    volumeListener = null;

    constructor(props){
        super(props)
        this.state = {
            volume: 0,
            brightness: 0,
            wifiEnable: false,
            wifiStateLoading: false,
            locationEnable: false,
            locationStateLoading: false,
        }
    }

    async componentDidMount(){
        this.setState({
            volume: await SystemSetting.getVolume(),
            brightness: await SystemSetting.getBrightness(),
            wifiEnable: await SystemSetting.isWifiEnabled(),
            locationEnable: await SystemSetting.isLocationEnabled(),
        })
        // just init slider value directly
        this._changeSliderNativeVol(this.sliderVol, this.state.volume)
        this._changeSliderNativeVol(this.sliderBri, this.state.brightness)

        this.volumeListener = SystemSetting.addVolumeListener((data) => {
            const volume = data.value
            this._changeSliderNativeVol(this.sliderVol, volume)
            this.setState({
                volume: volume
            })
        })
    }

    _changeSliderNativeVol(slider, value){
        slider.setNativeProps({
            value: value
        })
    }

    componentWillUnmount(){
        SystemSetting.removeVolumeListener(volumeListener)
    }

    _changeVol(value){
        SystemSetting.setVolume(value)
        this.setState({
            volume: value
        })
    }

    _changeBrightness(value){
        SystemSetting.setBrightnessForce(value)
        this.setState({
            brightness: value
        })
    }

    _restoreBrightness(){
        const saveBrightnessVal = SystemSetting.restoreBrightness()
        if(saveBrightnessVal > -1){
            // success
            this.setState({
                brightness: saveBrightnessVal
            })
            this._changeSliderNativeVol(this.sliderBri, saveBrightnessVal)
        }
    }

    _switchWifi(){
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

    _switchLocation(){
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

    render() {
        const {volume, brightness, wifiEnable, wifiStateLoading, locationEnable, locationStateLoading} = this.state
        return (
            <ScrollView style={styles.container}>
                <View style={styles.head}>
                </View>
                <ValueView
                    title='Volume'
                    value={volume}
                    changeVal={(val)=>this._changeVol(val)}
                    refFunc={(sliderVol)=>this.sliderVol = sliderVol}
                />
                <ValueView
                    title='Brightness'
                    value={brightness}
                    changeVal={(val)=>this._changeBrightness(val)}
                    refFunc={(sliderBri)=>this.sliderBri = sliderBri}
                />
                <View style={styles.card}>
                    <View style={styles.row}>
                        <Text style={styles.title}>Brightness save & restore
                        </Text>
                    </View>
                    <View style={styles.row}>
                        <TouchableOpacity style={{marginRight:32}} onPress={SystemSetting.saveBrightness}>
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
                    switchFunc={(value) => this._switchWifi()}/>
                <StatusView
                    title='Location'
                    value={locationEnable}
                    loading={locationStateLoading}
                    switchFunc={(value) => this._switchLocation()}/>
            </ScrollView>
        );
    }
}

const ValueView = (props)=>{
    const {title, value, changeVal, refFunc} = props
    return(
        <View style={styles.card}>
            <View style={styles.row}>
                <Text style={styles.title}>{title}</Text>
                <Text style={styles.value}>{value}</Text>
            </View>
            <Slider
                ref={refFunc}
                style={styles.slider}
                onValueChange={changeVal} />
        </View>
    )
}

const StatusView = (props)=>{
    const {title, switchFunc, value, loading} = props
    return(
        <View style={styles.card}>
            <View style={styles.row}>
                <Text style={styles.title}>{title}
                </Text>
            </View>
            <View style={styles.row}>
                <Text>Current status is { loading ? 'switching': (value ? 'On' : 'Off')}
                </Text>
                {
                    loading&&<ActivityIndicator animating={loading}/>
                }
                <View style={{flex:1, alignItems:'flex-end'}}>
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
    head:{
        justifyContent: 'center',
        alignItems: 'center',
        height: 64,
    },
    headline:{
        fontSize: 22,
        color: '#666'
    },
    card:{
        padding:8,
        backgroundColor: '#fff',
        marginTop:4,
        marginBottom:4,
    },
    row:{
        flexDirection:'row',
        alignItems:'center',
        paddingTop:8,
        paddingBottom:8,
    },
    title:{
        fontSize: 16,
        color: '#6F6F6F'
    },
    value:{
        fontSize: 14,
        flex:1,
        textAlign:'right',
        color: '#904ED9'
    },
    split:{
        marginVertical: 16,
        height: 1,
        backgroundColor: '#ccc',
    },
    btn:{
        fontSize: 16,
        padding: 8,
        paddingVertical: 8,
        color: '#405EFF'
    }
});

AppRegistry.registerComponent('SystemSettingExample', () => SystemSettingExample);
