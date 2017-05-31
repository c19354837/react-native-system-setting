/* @flow */

import {
  Platform
} from 'react-native';

export default class Utils {
    static isAndroid = Platform.OS === 'android'

    static isIOS = Platform.OS === 'ios'
}
