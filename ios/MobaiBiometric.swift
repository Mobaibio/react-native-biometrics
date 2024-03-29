import Foundation
import MobaiBiometric

public enum EventName: String, CaseIterable {
    case onCaptureFinished,
         onCaptureStarted,
         onWaitingToCaptureWithFaceStatus,
         onFailureWithErrorMessage,
         onPresentedDismissTapped,
         onCountDown,
         onValidation
    
    public var rawValue: String {
        switch self {
        case .onCaptureFinished:
            return "onCaptureFinished"
        case .onCaptureStarted:
            return "onCaptureStarted"
        case .onWaitingToCaptureWithFaceStatus:
            return "onWaitingToCaptureWithFaceStatus"
        case .onFailureWithErrorMessage:
            return "onFailureWithErrorMessage"
        case .onPresentedDismissTapped:
            return "onPresentedDismissTapped"
        case .onCountDown:
            return "onCountDown"
        case .onValidation:
            return "onValidation"
        }
    }
}

@objc (MobaiReactNative)
class MobaiReactNative: RCTEventEmitter, MBCaptureSessionVCDelegate  {
    
    func onFailure(error: MobaiBiometric.MBCaptureSessionError) {
        if error == .noFaceDetected {
            return
        }
        
        self.sendEvent(
            withName: EventName.onFailureWithErrorMessage.rawValue,
            body: [
                "errorDescription": error.errorDescription
            ]
        )
    }
    
    func onInitializing() {
    }
    
    func onValidation(_ status: MobaiBiometric.DetectedFaceStatus) {
        self.sendEvent(
            withName: EventName.onValidation.rawValue,
            body: [
                "status": status.rawValue
            ]
        )
    }
    
    func onCountDown(time: Int) {
        self.sendEvent(
            withName: EventName.onCountDown.rawValue,
            body: [
                "time": time
            ]
        )
    }
    
    func onCapturing() {
        self.sendEvent(
            withName: EventName.onCaptureStarted.rawValue,
            body: ""
        )
    }
    
    func onSuccess(result: MobaiBiometric.MBCaptureSessionResult) {
        self.sendEvent(
            withName: EventName.onCaptureFinished.rawValue,
            body: [
                "image": result.faceImage.base64EncodedString(),
                "padData": result.padData.map { $0.base64EncodedString() }
            ]
        )
        
        onPresentedDismissTapped()
    }
    
    
    override class func requiresMainQueueSetup() -> Bool {
        true
    }
    
    override func supportedEvents() -> [String]! {
        EventName.allCases.map(\.rawValue)
    }
    
    @objc
    override func constantsToExport() -> [AnyHashable : Any]! {
        [
            "EVENT_SUCCESS": EventName.onCaptureFinished.rawValue,
            "EVENT_FAILURE": EventName.onFailureWithErrorMessage.rawValue,
            "EVENT_CAPTURE_FINISHED": EventName.onCaptureFinished.rawValue,
            "EVENT_CAPTURE_STARTED": EventName.onCaptureStarted.rawValue,
        ]
    }
    
    @objc(launch:)
    func launch(
        options: NSDictionary
    ) {
        DispatchQueue.main.async {
            let sessionOptions = MBCaptureSessionOptions(dictionaryOptions: options)
            let captureVC = MBCaptureSessionViewController(options: sessionOptions)
            let navigation = UINavigationController(rootViewController: captureVC)
            navigation.modalPresentationStyle = .fullScreen
            captureVC.delegate = self
            
            guard let window = UIApplication.shared.delegate!.window!!.rootViewController else { return }
            window.present(navigation, animated: true)
        }
    }
    
    func onWaitingToCapture(faceStatus: MobaiBiometric.DetectedFaceStatus) {
        self.sendEvent(
            withName: EventName.onWaitingToCaptureWithFaceStatus.rawValue,
            body: [
                "faceStatus": faceStatus.rawValue
            ]
        )
    }
    
    func onCaptureStarted() {
        self.sendEvent(withName: EventName.onCaptureStarted.rawValue, body: nil)
    }
    
    func onCaptureFinished(images: Data, padData: [Data]) {
        self.sendEvent(
            withName: EventName.onCaptureFinished.rawValue,
            body: [
                "image": images.base64EncodedString(),
                "padData": padData.map { $0.base64EncodedString() }
            ]
        )
        
        onPresentedDismissTapped()
    }
    
    func onPresentedDismissTapped() {
        guard let window = UIApplication.shared.delegate!.window!!.rootViewController else { return }
        window.dismiss(animated: true)
    }
}

@objc
public class Options: NSObject {
    public var autoCaptureEnabled: Bool = true
    public var numberOfFramesBeforeCapture: Int = 10
    public var numberOfFrameToCollect: Int = 3
    public var frameInterval: Int = 10
    public var padCaptureEnabled: Bool = true
    public var faceQualityEnabled: Bool = false
    public var isDebugging: Bool = false
    public var timeBeforeAutomaticCapture: Int = 4
    public var cameraPosition: CameraPostion = .front
    public var presentedDismissButtonEnabled: Bool = false
    public var cameraPermissionAlert: CameraPermissionAlert = .init()
}
