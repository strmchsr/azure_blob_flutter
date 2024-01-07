
# Azure Blob Flutter Package

The azure_blob_flutter package provides a convenient and simple way to upload images and delete blobs from Azure Blob Storage in a Flutter application. It is designed to work seamlessly with Azure Blob Storage and aims to simplify common tasks associated with managing blobs.


## Installation

Install my-project with npm

```bash
 dependencies:
  azure_blob_flutter: ^0.0.5
```

## Usage/Examples

```javascript
final _azureBlobFlutterPlugin = AzureBlobFlutter("blobBaseUrl",
      "blobVideoContainerName",
      "blobImageContainerName");

 await _azureBlobFlutterPlugin.uploadImage("path", "fileName",
              "sasTokenUrl", false);

 await _azureBlobFlutterPlugin.delete("blobName",
              "sasTokenUrl", false);             
```


## License
This package is licensed under the MIT License - see the [LICENCE](https://pub.dev/packages/azure_blob_flutter/license) file for details.


