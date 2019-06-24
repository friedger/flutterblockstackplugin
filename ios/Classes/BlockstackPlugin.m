#import "BlockstackPlugin.h"
#import <blockstack/blockstack-Swift.h>

@implementation BlockstackPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBlockstackPlugin registerWithRegistrar:registrar];
}
@end
