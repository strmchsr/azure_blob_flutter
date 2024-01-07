import 'package:flutter_test/flutter_test.dart';
import 'package:azure_blob_flutter/azure_blob_flutter.dart';
import 'package:azure_blob_flutter/azure_blob_flutter_platform_interface.dart';
import 'package:azure_blob_flutter/azure_blob_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAzureBlobFlutterPlatform
    with MockPlatformInterfaceMixin
    implements AzureBlobFlutterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final AzureBlobFlutterPlatform initialPlatform = AzureBlobFlutterPlatform.instance;

  test('$MethodChannelAzureBlobFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAzureBlobFlutter>());
  });

  test('getPlatformVersion', () async {
    AzureBlobFlutter azureBlobFlutterPlugin = AzureBlobFlutter();
    MockAzureBlobFlutterPlatform fakePlatform = MockAzureBlobFlutterPlatform();
    AzureBlobFlutterPlatform.instance = fakePlatform;

    expect(await azureBlobFlutterPlugin.getPlatformVersion(), '42');
  });
}
