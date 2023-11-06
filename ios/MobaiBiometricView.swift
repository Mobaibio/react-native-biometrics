//
//  MobaiBiometricView.swift
//  mobai-biometric
//
//  Created by Dion Dula on 25.9.23.
//

import UIKit
import MobaiBiometric

class MyCustomView: MBCaptureSessionView, MBCaptureSessionDelegate {
    private struct Keys {
        static var captureFailed = "massageCaptureFailed"
        static var captureFinish = "messageCaptureFinished"
        static var capatureProgress = "messageCaptureProgress"
        static var capatureStarted = "messageCaptureStarted"
        static var messageFaceStatus = "messageFaceStatus"
        static var messageCaptureCountDown = "messageCaptureCountDown"
    }
    
    @objc var options: NSDictionary = .init() {
        didSet {
            let sessionOptions = MBCaptureSessionOptions(dictionaryOptions: options)
            onUpdate(options: sessionOptions)
        }
    }
    
    @objc var onChange: RCTBubblingEventBlock?
    override init(options: MBCaptureSessionOptions) {
        super.init(options: options)
        self.delegate = self
    }

    @MainActor required dynamic init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func onInitializing() { }
    
    func onValidating(_ status: MobaiBiometric.DetectedFaceStatus) {
        guard let onChange = self.onChange else { return }
        
        let params: [String : Any] = [Keys.messageFaceStatus: detectedFaceStatusToString(detectedFace: status)]
        onChange(params)
    }
    
    func onCountDown(time: Int) {
        guard let onChange = self.onChange else { return }
        
        let params: [String : Any] = [Keys.messageCaptureCountDown: String(time)]
        onChange(params)
    }
    
    func onCaptureStarted() {
        guard let onChange = self.onChange else { return }
        
        let params: [String : Any] = [Keys.capatureStarted: ""]
        onChange(params)
    }
    
    func onCaptureProgress(captureProgressCounter: Float) {
        guard let onChange = self.onChange else { return }
        
        let params: [String : Any] = [Keys.capatureProgress: captureProgressCounter]
        onChange(params)
    }
    
    func onCaptureFinished(result: MobaiBiometric.MBCaptureSessionResult) {
        guard let onChange = self.onChange else { return }
        
        let params: [String : Any] = [Keys.captureFinish: ["image": result.faceImage.base64EncodedString(),
                                         "padData": result.padData.map { $0.base64EncodedString() }
                                        ]]
        onChange(params)
    }
    
    func onFailure(error: MobaiBiometric.MBCaptureSessionError) {
        guard let onChange = self.onChange else { return }
        
        let params: [String : Any] = [Keys.captureFailed: error]
        onChange(params)
    }
    
    private func detectedFaceStatusToString(detectedFace: DetectedFaceStatus) -> String {
        switch detectedFace {
        case .faceToSmall:
            return "TOO_FAR_AWAY"
        case .faceToLarge:
            return "TOO_CLOSE"
        case .tooFarUp:
            return "TOO_FAR_UP"
        case .tooFarDown:
            return "TOO_FAR_DOWN"
        case .tooFarLeft:
            return "TOO_FAR_LEFT"
        case .tooFarRight:
            return "TOO_FAR_RIGHT"
        case .faceNotFound:
            return "NOT_FOUND"
        case .mouthNotFound:
            return "MouthNotFound"
        case .validFace:
            return "VALID"
        @unknown default:
            return ""
        }
    }
}

@objc (RCTMBCaptureSessionViewManager)
class RCTMBCaptureSessionViewManager: RCTViewManager {
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    @MainActor
    override func view() -> UIView {
        let captureSessionView = MyCustomView(options: .init(isDebugging: true))
        captureSessionView.onStartCapturing()
        
        return captureSessionView
    }
}
