# Azure Blob Flutter

A Flutter plugin for uploading and deleting files from Microsoft Azure Blob Storage.

## Features

- Upload files to Azure Blob Storage.
- Delete files from Azure Blob Storage.
- Support for both Android and iOS.

## Installation

Add the following to your `pubspec.yaml` file:

```yaml
dependencies:
  azure_blob_flutter: ^0.0.10
```

Then run `flutter pub get`.

## Usage

Import the package in your Dart file:

```dart
import 'package:azure_blob_flutter/azure_blob_flutter.dart';
```

Initialize the plugin:

```dart
final _azureBlobFlutter = AzureBlobFlutter(
  'YOUR_BLOB_BASE_URL',
  'YOUR_VIDEO_CONTAINER_NAME',
  'YOUR_IMAGE_CONTAINER_NAME'
);
```

### Upload a File

```dart
Future<void> upload(String filePath, String fileName, String sasToken, bool isVideo) async {
  try {
    String? blobUrl = await _azureBlobFlutter.uploadImage(
        filePath, fileName, sasToken, isVideo);
    print('Uploaded: $blobUrl');
  } catch (e) {
    print(e);
  }
}
```

### Delete a File

```dart
Future<void> delete(String blobName, String sasToken, bool isVideo) async {
  try {
    await _azureBlobFlutter.delete(blobName, sasToken, isVideo);
    print('Delete request sent for $blobName.');
  } catch (e) {
    print(e);
  }
}
```
*Note: The delete functionality on iOS is not fully implemented.*

## License

This package is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.


