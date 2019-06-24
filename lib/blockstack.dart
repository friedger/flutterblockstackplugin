import 'dart:async';

import 'package:flutter/services.dart';

class Blockstack {
  static const MethodChannel _channel =
      const MethodChannel('blockstack');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> createSession (appDomain, manifestPath, redirectPath, scopes) async {
    await _channel.invokeMethod('createSession', <String, dynamic>{
      'appDomain':appDomain,
      'manifestPath':manifestPath,
      'redirectPath':redirectPath,
      //'scopes':scopes
    });
    return "ok";
  }


}
