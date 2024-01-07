import 'azure_blob_flutter_platform_interface.dart';

class AzureBlobFlutter {
  final String blobBaseUrl;
  final String blobVideoContainerName;
  final String blobImageContainerName;

  AzureBlobFlutter(
    this.blobBaseUrl,
    this.blobVideoContainerName,
    this.blobImageContainerName,
  );

  Future<String?> uploadImage(
    String path,
    String fileName,
    String sasToken,
    bool isVideo,
  ) {
    return AzureBlobFlutterPlatform.instance.uploadImage(
        path,
        fileName,
        isVideo,
        blobBaseUrl,
        blobVideoContainerName,
        blobImageContainerName,
        sasToken);
  }

  Future<String?> delete(
    String blobName,
    String sasToken,
    bool isVideo,
  ) {
    return AzureBlobFlutterPlatform.instance.delete(blobName, isVideo,
        blobBaseUrl, blobVideoContainerName, blobImageContainerName, sasToken);
  }
}
