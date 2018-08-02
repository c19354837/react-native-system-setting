//
//  RTCSystemSetting.m
//  RTCSystemSetting
//
//  Created by ninty on 2017/5/29.
//  Copyright © 2017年 ninty. All rights reserved.
//

#import "RTCSystemSetting.h"
#import <SystemConfiguration/CaptiveNetwork.h>
#import <CoreLocation/CoreLocation.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <ifaddrs.h>
#import <net/if.h>

@import UIKit;
@import MediaPlayer;

@implementation RCTSystemSetting{
    bool hasListeners;
    CBCentralManager *cb;
    NSDictionary *setting;
    MPVolumeView *volumeView;
    UISlider *volumeSlider;
}

-(instancetype)init{
    self = [super init];
    if(self){
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(volumeChanged:)
                                                     name:@"AVSystemController_SystemVolumeDidChangeNotification"
                                                   object:nil];

        cb = [[CBCentralManager alloc] initWithDelegate:nil queue:nil options:@{CBCentralManagerOptionShowPowerAlertKey: @NO}];
    }

    [self initVolumeView];
    [self initSetting];

    return self;
}

-(void)initVolumeView{
    volumeView = [[MPVolumeView alloc] initWithFrame:CGRectMake(-[UIScreen mainScreen].bounds.size.width, 0, 0, 0)];
    [self showVolumeUI:YES];
    for (UIView* view in volumeView.subviews) {
        if ([view.class.description isEqualToString:@"MPVolumeSlider"]){
            volumeSlider = (UISlider*)view;
            break;
        }
    }
}

-(void)initSetting{
    BOOL newSys = [UIDevice currentDevice].systemVersion.doubleValue >= 10.0;
    setting = @{@"wifi": (newSys?@"QXBwLVByZWZzOnJvb3Q9V0lGSQ==" : @"cHJlZnM6cm9vdD1XSUZJ"),
                @"location": (newSys?@"QXBwLVByZWZzOnJvb3Q9UHJpdmFjeSZwYXRoPUxPQ0FUSU9O" : @"cHJlZnM6cm9vdD1Qcml2YWN5JnBhdGg9TE9DQVRJT04="),
                @"bluetooth": (newSys?@"QXBwLVByZWZzOnJvb3Q9Qmx1ZXRvb3Ro" : @"cHJlZnM6cm9vdD1CbHVldG9vdGg="),
                @"airplane": (newSys?@"QXBwLVByZWZzOnJvb3Q9QUlSUExBTkVfTU9ERQ==" : @"cHJlZnM6cm9vdD1BSVJQTEFORV9NT0RF")
                };
}

+(BOOL)requiresMainQueueSetup{
    return YES;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(setBrightness:(float)val resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    [[UIScreen mainScreen] setBrightness:val];
    resolve([NSNumber numberWithBool:YES]);
}

RCT_EXPORT_METHOD(getBrightness:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithDouble:[UIScreen mainScreen].brightness]);
}

RCT_EXPORT_METHOD(setVolume:(float)val config:(NSDictionary *)config){
    dispatch_sync(dispatch_get_main_queue(), ^{
        id showUI = [config objectForKey:@"showUI"];
        [self showVolumeUI:(showUI != nil && [showUI boolValue])];
        volumeSlider.value = val;
    });
}

RCT_EXPORT_METHOD(getVolume:(NSString *)type resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    dispatch_sync(dispatch_get_main_queue(), ^{
        resolve([NSNumber numberWithFloat:[volumeSlider value]]);
    });
}

RCT_EXPORT_METHOD(switchWifi){
    [self openSetting:@"wifi"];
}

RCT_EXPORT_METHOD(isWifiEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithBool:[self isWifiEnabled]]);
}

RCT_EXPORT_METHOD(switchLocation){
    [self openSetting:@"location"];
}

RCT_EXPORT_METHOD(isLocationEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithBool:[CLLocationManager locationServicesEnabled]]);
}

RCT_EXPORT_METHOD(switchBluetooth){
    [self openSetting:@"bluetooth"];
}

RCT_EXPORT_METHOD(isBluetoothEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    bool isEnabled = cb.state == CBManagerStatePoweredOn;
    resolve([NSNumber numberWithBool:isEnabled]);
}

RCT_EXPORT_METHOD(switchAirplane){
    [self openSetting:@"airplane"];
}

RCT_EXPORT_METHOD(isAirplaneEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    NSString * radio = [[CTTelephonyNetworkInfo alloc] init].currentRadioAccessTechnology;
    bool isEnabled = radio == nil;
    resolve([NSNumber numberWithBool:isEnabled]);
}

RCT_EXPORT_METHOD(activeListener:(NSString *)type resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    if([type isEqualToString:@"bluetooth"]){
        [cb setDelegate:self];
        resolve(@YES);
    }else{
         reject(@"-1", [NSString stringWithFormat:@"unsupported listener type: %@", type], nil);
    }
}

-(void)showVolumeUI:(BOOL)flag{
    if(flag && [volumeView superview]){
        [volumeView removeFromSuperview];
    }else if(!flag && ![volumeView superview]){
        [[[[UIApplication sharedApplication] keyWindow] rootViewController].view addSubview:volumeView];
    }
}

-(void)openSetting:(NSString*)service{
    NSString *url = [self dencodeStr:[setting objectForKey:service]];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url] options:[NSDictionary new] completionHandler:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationWakeUp:)
                                                 name:UIApplicationWillEnterForegroundNotification
                                               object:nil];
}

- (NSString *)dencodeStr:(NSString *)string{
    NSData *data = [[NSData alloc]initWithBase64EncodedString:string options:NSDataBase64DecodingIgnoreUnknownCharacters];
    NSString *result = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    return result;
}

-(BOOL)isWifiEnabled{
    NSCountedSet * cset = [NSCountedSet new];
    struct ifaddrs *interfaces;
    if( ! getifaddrs(&interfaces) ) {
        for( struct ifaddrs *interface = interfaces; interface; interface = interface->ifa_next)
        {
            if ( (interface->ifa_flags & IFF_UP) == IFF_UP ) {
                [cset addObject:[NSString stringWithUTF8String:interface->ifa_name]];
            }
        }
    }
    return [cset countForObject:@"awdl0"] > 1 ? YES : NO;
}

-(void)applicationWakeUp:(NSNotification*)notification{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
    [self sendEventWithName:@"EventEnterForeground" body:nil];
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"EventVolume", @"EventEnterForeground", @"EventBluetoothChange"];
}

-(void)startObserving {
    hasListeners = YES;
}

-(void)stopObserving {
    hasListeners = NO;
}

-(void)volumeChanged:(NSNotification *)notification{
    if(hasListeners){
        float volume = [[[notification userInfo] objectForKey:@"AVSystemController_AudioVolumeNotificationParameter"] floatValue];
        [self sendEventWithName:@"EventVolume" body:@{@"value": [NSNumber numberWithFloat:volume]}];
    }
}

-(void)centralManagerDidUpdateState:(CBCentralManager *)central{
    switch (central.state) {
        case CBManagerStatePoweredOff:
            if(hasListeners){
                [self sendEventWithName:@"EventBluetoothChange" body:@NO];
            }
            break;
        case CBManagerStatePoweredOn:
            if(hasListeners){
                [self sendEventWithName:@"EventBluetoothChange" body:@YES];
            }
            break;
        default:
            break;
    }
}

@end
