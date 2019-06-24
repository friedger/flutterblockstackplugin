import 'dart:async';

import 'package:flutter/services.dart';

class Blockstack {
  static const MethodChannel _channel = const MethodChannel('blockstack');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> createSession(
      appDomain, manifestPath, redirectPath, scopes) async {
    await _channel.invokeMethod('createSession', <String, dynamic>{
      'appDomain': appDomain,
      'manifestPath': manifestPath,
      'redirectPath': redirectPath,
      'scopes': scopes
    });
    return "ok";
  }

  static Future<String> redirectToSignIn() async {
    await _channel.invokeMethod("redirectToSignIn");
    return "ok";
  }

  static Future<String> handlePendingSignIn(authResponse) async {
    dynamic userData = await _channel.invokeMethod(
        "handlePendingSignIn", <String, dynamic>{'authResponse': authResponse});
    return userData.toString();
  }

  static Future<bool> isUserSignedIn() async {
    bool signedIn = await _channel.invokeMethod("isUserSignedIn");
    return signedIn;
  }

  static Future<String> loadUserData() async {
    String userData = await _channel.invokeMethod("loadUserData");
    return userData;
  }
}
