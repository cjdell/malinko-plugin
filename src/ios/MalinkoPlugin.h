#import <Cordova/CDVPlugin.h>

@interface MalinkoPlugin : CDVPlugin {
}

// The hooks for our plugin commands
- (void)getSilentAlarmStatus:(CDVInvokedUrlCommand *)command;
- (void)enableSilentAlarm:(CDVInvokedUrlCommand *)command;
- (void)disableSilentAlarm:(CDVInvokedUrlCommand *)command;

@end
