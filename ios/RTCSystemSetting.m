//
//  RTCSystemSetting.m
//  RTCSystemSetting
//
//  Created by ninty on 2017/5/29.
//  Copyright © 2017年 ninty. All rights reserved.
//

#import "RTCSystemSetting.h"
@import UIKit;
@import MediaPlayer;

@implementation RCTSystemSetting

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(setBrightness:(float)val){
    [[UIScreen mainScreen] setBrightness:val];
}

RCT_EXPORT_METHOD(getBrightness:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithDouble:[UIScreen mainScreen].brightness]);
}

RCT_EXPORT_METHOD(setScreenMode:(float)val){
    //do nothing
}

RCT_EXPORT_METHOD(getScreenMode:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithDouble:0]);
}

RCT_EXPORT_METHOD(setVolume:(float)val){
    [[MPMusicPlayerController applicationMusicPlayer] setVolume:val];
}

RCT_EXPORT_METHOD(getVolume:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithDouble:[MPMusicPlayerController applicationMusicPlayer].volume]);
}

@end
