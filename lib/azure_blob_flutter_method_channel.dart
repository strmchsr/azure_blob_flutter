import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'azure_blob_flutter_platform_interface.dart';

/// An implementation of [AzureBlobFlutterPlatform] that uses method channels.
class MethodChannelAzureBlobFlutter extends AzureBlobFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('azure_blob_flutter');

  @override
  Future<String?> uploadImage(
      String path,
      String fileName,
      bool isVideo,
      String blobBaseUrl,
      String blobVideoContainerName,
      String blobImageContainerName,
      String sasToken) async {
    final version = await methodChannel.invokeMethod<String>('uploadImage', {
      'path': path,
      'fileName': fileName,
      'isVideo': isVideo,
      'blobBaseUrl': blobBaseUrl,
      'blobVideoContainerName': blobVideoContainerName,
      'blobImageContainerName': blobImageContainerName,
      'sasToken': sasToken,
    });
    return version;
  }

  @override
  Future<String?> delete(
      String blobName,
      bool isVideo,
      String blobBaseUrl,
      String blobVideoContainerName,
      String blobImageContainerName,
      String sasToken) async {
    final version = await methodChannel.invokeMethod<String>('delete', {
      'blobName': blobName,
      'isVideo': isVideo,
      'blobBaseUrl': blobBaseUrl,
      'blobVideoContainerName': blobVideoContainerName,
      'blobImageContainerName': blobImageContainerName,
      'sasToken': sasToken,
    });
    return version;
  }
}
