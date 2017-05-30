import React, {Component} from 'react';
import {AppRegistry, StyleSheet, Text, View, Slider} from 'react-native';

import SystemSetting from 'react-native-system-setting'

export default class SystemSettingExample extends Component {

    constructor(props){
        super(props)
        this.state = {
            volume: 0,
            brightness: 0
        }
    }

    async componentDidMount(){
        this.setState({
            volume: await SystemSetting.getVolume(),
            brightness: await SystemSetting.getBrightness()
        })
        // just init slider value directly
        this.sliderVol.setNativeProps({
            value: this.state.volume
        })
        this.sliderBri.setNativeProps({
            value: this.state.brightness
        })
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

    render() {
        return (
            <View style={styles.container}>
                <View>
                </View>
                <View style={styles.card}>
                    <View style={styles.row}>
                        <Text style={styles.title}>Volume</Text>
                        <Text style={styles.value}>{this.state.volume}</Text>
                    </View>
                    <Slider
                        ref={(sliderVol)=>this.sliderVol = sliderVol}
                        style={styles.slider}
                        onValueChange={this._changeVol.bind(this)} />
                </View>
                <View style={styles.card}>
                    <View style={styles.row}>
                        <Text style={styles.title}>Brightness</Text>
                        <Text style={styles.value}>{this.state.brightness}</Text>
                    </View>
                    <Slider
                        ref={(sliderBri)=>this.sliderBri = sliderBri}
                        style={styles.slider}
                        onValueChange={this._changeBrightness.bind(this)} />
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        paddingTop: 64,
        backgroundColor: '#E5E7E8'
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
});

AppRegistry.registerComponent('SystemSettingExample', () => SystemSettingExample);
