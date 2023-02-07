#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(MobaiReactNative, RCTEventEmitter)

RCT_EXTERN_METHOD(launch:(NSDictionary)options)

@end
