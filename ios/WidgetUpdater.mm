#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(WidgetUpdater, NSObject)

RCT_EXTERN_METHOD(getAppWidgetIds:(NSArray)widgetClasses
                  withResolver:(RCTPromiseResolveBlock)resolve
                  withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(updateAppWidgets:(NSArray)widgetClasses
                  withResolver:(RCTPromiseResolveBlock)resolve
                  withRejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

@end
