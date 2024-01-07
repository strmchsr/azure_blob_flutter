package com.gigjamgo.azure_blob_flutter

import com.azure.storage.blob.BlobServiceClientBuilder
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File

/** AzureBlobFlutterPlugin */
class AzureBlobFlutterPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "azure_blob_flutter")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "uploadImage" -> {
                val filePath = call.argument<String?>("path")
                val fileName = call.argument<String?>("fileName")
                val isVideo = call.argument<Boolean?>("isVideo")
                val blobBaseUrl = call.argument<String?>("blobBaseUrl")
                val blobVideoContainerName = call.argument<String?>("blobVideoContainerName")
                val blobImageContainerName = call.argument<String?>("blobImageContainerName")
                val sasToken = call.argument<String?>("sasToken")
                result.success(
                    uploadToAzure(
                        filePath,
                        fileName,
                        isVideo,
                        blobBaseUrl,
                        blobVideoContainerName,
                        blobImageContainerName,
                        sasToken
                    )
                )
            }

            "delete" -> {
                val blobName = call.argument<String?>("blobName")
                val isVideo = call.argument<Boolean?>("isVideo")
                val blobBaseUrl = call.argument<String?>("blobBaseUrl")
                val blobVideoContainerName = call.argument<String?>("blobVideoContainerName")
                val blobImageContainerName = call.argument<String?>("blobImageContainerName")
                val sasToken = call.argument<String?>("sasToken")
                result.success(
                    delete(
                        blobName,
                        isVideo,
                        blobBaseUrl,
                        blobVideoContainerName,
                        blobImageContainerName,
                        sasToken
                    )
                )
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun uploadToAzure(
        path: String?,
        fileName: String?,
        isVideo: Boolean?,
        blobBaseUrl: String?,
        blobVideoContainerName: String?,
        blobImageContainerName: String?,
        sasToken: String?
    ): String {
        if (path == null) {
            return ""
        }
        val endpoint = "${blobBaseUrl}?${sasToken}"
        val blobServiceClient = BlobServiceClientBuilder()
            .endpoint(endpoint)
            .buildClient()

        // Extract the container name and blob name from the SAS URL
        val containerName = if (isVideo == true) blobVideoContainerName else blobImageContainerName
        val blobName =
            fileName?.split(".")?.first() + System.currentTimeMillis() + "." + fileName?.split(".")
                ?.last()

        // Get a reference to the container and blob
        val containerClient = blobServiceClient.getBlobContainerClient(containerName)
        val blobClient = containerClient.getBlobClient(blobName)

        // Upload the file
        try {
            val file = File(path)
            blobClient.uploadFromFile(file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return blobName
    }

    private fun delete(
        blobName: String?,
        isVideo: Boolean?,
        blobBaseUrl: String?,
        blobVideoContainerName: String?,
        blobImageContainerName: String?,
        sasToken: String?
    ): String {
        val endpoint = "${blobBaseUrl}?${sasToken}"
        val blobServiceClient = BlobServiceClientBuilder()
            .endpoint(endpoint)
            .buildClient()

        // Extract the container name and blob name from the SAS URL
        val containerName = if (isVideo == true) blobVideoContainerName else blobImageContainerName
        // Get a reference to the container and blob
        val containerClient = blobServiceClient.getBlobContainerClient(containerName)
        val blobClient = containerClient.getBlobClient(blobName)

        // Upload the file
        try {
            blobClient.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Deleted"
    }
}
