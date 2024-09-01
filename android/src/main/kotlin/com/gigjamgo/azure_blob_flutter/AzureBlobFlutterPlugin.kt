package com.gigjamgo.azure_blob_flutter

import android.util.Log
import com.azure.core.util.Context
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.models.AccessTier
import com.azure.storage.blob.options.BlobUploadFromFileOptions
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.time.Duration


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

                CoroutineScope(Dispatchers.IO).launch {
                    val response = uploadToAzure(
                        filePath,
                        fileName,
                        isVideo,
                        blobBaseUrl,
                        blobVideoContainerName,
                        blobImageContainerName,
                        sasToken
                    )

                    withContext(Dispatchers.Main) {
                        if (response.second) {
                            result.success(response.first)
                        } else {
                            result.error(response.first, "", "")
                        }
                    }
                }
            }

            "delete" -> {
                val blobName = call.argument<String?>("blobName")
                val isVideo = call.argument<Boolean?>("isVideo")
                val blobBaseUrl = call.argument<String?>("blobBaseUrl")
                val blobVideoContainerName = call.argument<String?>("blobVideoContainerName")
                val blobImageContainerName = call.argument<String?>("blobImageContainerName")
                val sasToken = call.argument<String?>("sasToken")

                CoroutineScope(Dispatchers.IO).launch {
                    val deleteResponse = delete(
                        blobName,
                        isVideo,
                        blobBaseUrl,
                        blobVideoContainerName,
                        blobImageContainerName,
                        sasToken
                    )

                    withContext(Dispatchers.Main) {
                        result.success(deleteResponse)
                    }
                }
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
    ): Pair<String, Boolean> {
        if (path == null) {
            return Pair("Path is null", false)
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
            var result: Pair<String, Boolean>? = null
            runBlocking {
                val job = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = blobClient.uploadFromFileWithResponse(
                            BlobUploadFromFileOptions(file.path),
                            Duration.ofMinutes(5),
                            Context("", "")
                        )

                        if (response.statusCode == 201) {
                            result = Pair(blobName, true)
                        } else {
                            result = Pair("Upload failed", false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        result = Pair(e.localizedMessage ?: e.toString(), false)
                    }
                }
                job.join()
            }
            return result ?: Pair("Unknown error", false)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(e.localizedMessage ?: e.toString(), false)
        }
    }

    suspend fun delete(
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
        return withContext(Dispatchers.IO) {
            try {
                blobClient.delete()
                "Deleted"
            } catch (e: Exception) {
                e.printStackTrace()
                "Failed to delete"
            }
        }
    }
}
