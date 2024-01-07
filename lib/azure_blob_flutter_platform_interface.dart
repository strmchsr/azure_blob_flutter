import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'azure_blob_flutter_method_channel.dart';

abstract class AzureBlobFlutterPlatform extends PlatformInterface {
  /// Constructs a AzureBlobFlutterPlatform.
  AzureBlobFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static AzureBlobFlutterPlatform _instance = MethodChannelAzureBlobFlutter();

  /// The default instance of [AzureBlobFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelAzureBlobFlutter].
  static AzureBlobFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AzureBlobFlutterPlatform] when
  /// they register themselves.
  static set instance(AzureBlobFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> uploadImage(
      String path,
      String fileName,
      bool isVideo,
      String blobBaseUrl,
      String blobVideoContainerName,
      String blobImageContainerName,
      String sasToken) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> delete(
      String blobName,
      bool isVideo,
      String blobBaseUrl,
      String blobVideoContainerName,
      String blobImageContainerName,
      String sasToken) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
