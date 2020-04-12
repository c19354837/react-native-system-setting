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
#import <ifaddrs.h>
#import <net/if.h>

#ifdef BLUETOOTH
#import <CoreBluetooth/CoreBluetooth.h>
#endif

@import UIKit;
@import MediaPlayer;

@interface RCTSystemSetting()
#ifdef BLUETOOTH
<CBCentralManagerDelegate>
#endif
@end

@implementation RCTSystemSetting {
    bool hasListeners;
    long skipSetVolumeCount;
#ifdef BLUETOOTH
    CBCentralManager *cb;
#endif
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
#ifdef BLUETOOTH
        cb = [[CBCentralManager alloc] initWithDelegate:nil queue:nil options:@{CBCentralManagerOptionShowPowerAlertKey: @NO}];
#endif
    }

    [self initVolumeView];
#ifdef PRIVATE_API
    [self initSetting];
#endif

    return self;
}

-(void)initVolumeView{
    skipSetVolumeCount = 0;
    volumeView = [[MPVolumeView alloc] initWithFrame:CGRectMake(-[UIScreen mainScreen].bounds.size.width, 0, 0, 0)];
    [self showVolumeUI:YES];
    for (UIView* view in volumeView.subviews) {
        if ([view.class.description isEqualToString:@"MPVolumeSlider"]){
            volumeSlider = (UISlider*)view;
            break;
        }
    }
}

#ifdef PRIVATE_API
-(void)initSetting{
    BOOL newSys = [UIDevice currentDevice].systemVersion.doubleValue >= 10.0;
    setting = @{@"wifi": (newSys?@"App-Prefs:root=WIFI" : @"prefs:root=WIFI"),
                @"location": (newSys?@"App-Prefs:root=Privacy&path=LOCATION" : @"prefs:root=Privacy&path=LOCATION"),
                @"bluetooth": (newSys?@"App-Prefs:root=Bluetooth" : @"prefs:root=Bluetooth"),
                @"airplane": (newSys?@"App-Prefs:root=AIRPLANE_MODE" : @"prefs:root=AIRPLANE_MODE")
                };
}
#endif

+(BOOL)requiresMainQueueSetup{
    return YES;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(setBrightness:(float)val resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    dispatch_sync(dispatch_get_main_queue(), ^{
        [[UIScreen mainScreen] setBrightness:val];
        resolve([NSNumber numberWithBool:YES]);
    });
}

RCT_EXPORT_METHOD(getBrightness:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    resolve([NSNumber numberWithDouble:[UIScreen mainScreen].brightness]);
}

RCT_EXPORT_METHOD(setVolume:(float)val config:(NSDictionary *)config){
    skipSetVolumeCount++;
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
#ifdef BLUETOOTH
    bool isEnabled = cb.state == CBManagerStatePoweredOn;
    resolve([NSNumber numberWithBool:isEnabled]);
#else
    NSLog(@"You need add BLUETOOTH in preprocess macros, see https://github.com/c19354837/react-native-system-setting/blob/master/iOS.md");
    resolve([NSNumber numberWithBool:NO]);
#endif
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
#ifdef BLUETOOTH
        [cb setDelegate:self];
#endif
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
#ifdef PRIVATE_API
    NSString *url = [setting objectForKey:service];
    dispatch_sync(dispatch_get_main_queue(), ^{
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url] options:[NSDictionary new] completionHandler:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(applicationWakeUp:)
                                                     name:UIApplicationWillEnterForegroundNotification
                                                   object:nil];
    });
#else
    NSLog(@"Fail to open [%@]. These APIs which start with 'switch*()' will cause a rejection from App Store, and you can use these APIs only when you distribute app outside App Store, see see https://github.com/c19354837/react-native-system-setting/blob/master/iOS.md", service);
#endif
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
    if(skipSetVolumeCount == 0 && hasListeners){
        float volume = [[[notification userInfo] objectForKey:@"AVSystemController_AudioVolumeNotificationParameter"] floatValue];
        [self sendEventWithName:@"EventVolume" body:@{@"value": [NSNumber numberWithFloat:volume]}];
    }
    if(skipSetVolumeCount > 0){
        skipSetVolumeCount--;
    }
}

#ifdef BLUETOOTH
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
#endif

@end
