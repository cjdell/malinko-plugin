#import "MalinkoPlugin.h"

#import <Cordova/CDVAvailability.h>

@implementation MalinkoPlugin

- (void)pluginInitialize {
}

- (void)getSilentAlarmStatus:(CDVInvokedUrlCommand *)command {
  // TODO: iOS equivalent
  CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:NO];
  [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)enableSilentAlarm:(CDVInvokedUrlCommand *)command {
  // TODO: iOS equivalent
  CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
  [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)disableSilentAlarm:(CDVInvokedUrlCommand *)command {
  // TODO: iOS equivalent
  CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
  [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

@end
