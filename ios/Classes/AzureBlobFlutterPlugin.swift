import Flutter
import UIKit
import AZSClient

public class AzureBlobFlutterPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "azure_blob_flutter", binaryMessenger: registrar.messenger())
        let instance = AzureBlobFlutterPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "uploadImage":
            if let arguments = call.arguments as? [String: Any] {
                let filePath = arguments["path"] as? String
                let fileName = arguments["fileName"] as? String
                let isVideo = arguments["isVideo"] as? Bool
                let blobBaseUrl = arguments["blobBaseUrl"] as? String
                let blobVideoContainerName = arguments["blobVideoContainerName"] as? String
                let blobImageContainerName = arguments["blobImageContainerName"] as? String
                let sasToken = arguments["sasToken"] as? String
               let response =  uploadToAzure(path: filePath, fileName: fileName, isVideo: isVideo, blobBaseUrl: blobBaseUrl, blobVideoContainerName: blobVideoContainerName, blobImageContainerName: blobImageContainerName, sasToken: sasToken)
                result(response)
            }
            
        case "delete":
            if let arguments = call.arguments as? [String: Any] {
                let blobName = arguments["blobName"] as? String
                let isVideo = arguments["isVideo"] as? String
                let blobBaseUrl = arguments["blobBaseUrl"] as? String
                let blobVideoContainerName = arguments["blobVideoContainerName"] as? String
                let blobImageContainerName = arguments["blobImageContainerName"] as? String
                let sasToken = arguments["sasToken"] as? String
                
            }
            result("iOS " + UIDevice.current.systemVersion)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    private func uploadToAzure(path :String?,
                               fileName: String?,
                                       isVideo: Bool?,
                                       blobBaseUrl: String?,
                               blobVideoContainerName: String?,
                                       blobImageContainerName: String?,
                                       sasToken: String?
    ) -> String {
        var blobName: String?
        do {
            var containerURL = ""
            if let blobBaseUrlUnwrapped = blobBaseUrl {
                // optionalString is not nil, unwrappedString contains the non-nil value
                containerURL += blobBaseUrlUnwrapped
            } else {
                return "Base url can't be null"
            }
            
            if let blobImageContainerNameUnwrapped = blobImageContainerName {
                // optionalString is not nil, unwrappedString contains the non-nil value
                containerURL += blobImageContainerNameUnwrapped
            } else {
                return "Container name can't be null"
            }
            
            if let sasTokenUnwrapped = sasToken {
                // optionalString is not nil, unwrappedString contains the non-nil value
                containerURL += "?"+sasTokenUnwrapped
            } else {
                return "SAS token name can't be null"
            }
            
            print("containerURL with SAS: \(containerURL) ")
            let account = try AZSCloudStorageAccount(fromConnectionString:containerURL)
            let blobClient: AZSCloudBlobClient = account.getBlobClient()
            var container : AZSCloudBlobContainer = blobClient.containerReference(fromName: blobImageContainerName!)
            var error: NSError?
                        
            if let fileName = fileName {
                let fileNameComponents = fileName.components(separatedBy: ".")
                if let firstComponent = fileNameComponents.first, let lastComponent = fileNameComponents.last {
                    let currentTimeMillis = Int64(Date().timeIntervalSince1970 * 1000)
                    blobName = "\(firstComponent)\(currentTimeMillis).\(lastComponent)"
                }
            } else {
                return "File name can't be null"
            }
            if ((error) != nil) {
                print("Error in creating blob container object.  Error code = %ld, error domain = %@, error userinfo = %@", error!.code, error!.domain, error!.userInfo);
            }
            else {
                let blob = container.blockBlobReference(fromName: blobName!)
                blobName = blob.blobName
                blob.uploadFromFile(withPath: path!, completionHandler: {(NSError) -> Void in
                    NSLog("Ok, uploaded !")
                })
            }
        } catch {
            print("Error: \(error)")
        }
        return blobName!
    }
}
