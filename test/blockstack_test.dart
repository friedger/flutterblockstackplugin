import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:blockstack/blockstack.dart';

void main() {
  const MethodChannel channel = MethodChannel('blockstack');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Blockstack.platformVersion, '42');
  });
}
