import 'dart:async';

import 'package:blockstack/blockstack.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _blockstackStatus = 'Unknown';
  String _did = '';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String result;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      result = await Blockstack.createSession(
          "https://app-center.openintents.org",
          "/manifest.webmanifest",
          "/app",
          "store_write,email");
    } on PlatformException {
      result = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _blockstackStatus = result;
    });
  }

  Future<void> _login() async {
    var signedIn = await Blockstack.isUserSignedIn();
    if (!signedIn) {
      Blockstack.redirectToSignIn();
    } else {
      var did = await Blockstack.loadUserData();
      setState(() {
        _did = did;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Blockstack Flutter Example'),
          ),
          body: Center(
            child: Text.rich(
              TextSpan(
                text: 'Blockstack Session: $_blockstackStatus\n', // default text style
                children: <TextSpan>[
                  TextSpan(text: '$_did ', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, shadows: [Shadow(color: Color.fromARGB(150, 155, 155, 155),offset: Offset(4, 4))]))
                ],
              ),
            )
          ),
          floatingActionButton: FloatingActionButton(
            onPressed: _login,
            tooltip: 'Login',
            child: Icon(Icons.face),
          )),
    );
  }
}
