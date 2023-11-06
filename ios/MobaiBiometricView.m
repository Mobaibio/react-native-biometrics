//
//  SampleView.m
//  mobai-biometric
//
//  Created by Dion Dula on 25.9.23.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(RCTMBCaptureSessionViewManager, RCTViewManager)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(options, NSDictionary)
@end
