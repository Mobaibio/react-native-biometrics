import { MobaiBiometric } from './NativeModule';
import type { PreviewScaleType } from './Utils';
export const { EVENT_SUCCESS, EVENT_FAILURE } = MobaiBiometric.getConstants();

/*
     launch executes the  the Biometric Capture module
    */
export function launch(options: MobaiBiometricOptions): void {
  MobaiBiometric.launch({
    numberOfFramesBeforeCapture: options.numberOfFramesBeforeCapture,
    numberOfFrameToCollect: options.numberOfFrameToCollect,
    frameInterval: options.frameInterval,
    padCaptureEnabled: options.padCaptureEnabled,
    faceQualityEnabled: options.faceQualityEnabled,
    timeBeforeAutomaticCapture: options.timeBeforeAutomaticCapture,
    isDebugging: options.isDebugging,
    cameraPermissionAlert: options.cameraPermissionAlert,
    faceStatusTexts: options.faceStatusTexts,
    previewScaleType: options.previewScaleType,
    showCountdownLabel: options.showCountdownLabel,
    showProgressBar: options.showProgressBar,
    showFaceStatusLabel: options.showFaceStatusLabel,
    countdownLabelText: options.countdownLabelText,
  });
}

export interface FaceStatusTexts {
  faceTooFarAway: string;
  faceTooFarUp: string;
  faceTooFarDown: string;
  faceTooFarLeft: string;
  faceTooFarRight: string;
  faceTooClose: string;
  faceNotFound: string;
  tooManyFaces: string;
  validFace: string;
}

/*
    MobaiBiometricOptions is the options structure for the Biometric Capture module
  */
export interface MobaiBiometricOptions {
  numberOfFramesBeforeCapture?: number;
  numberOfFrameToCollect?: number;
  frameInterval?: number;
  padCaptureEnabled?: boolean;
  faceQualityEnabled?: boolean;
  timeBeforeAutomaticCapture?: number;
  isDebugging?: boolean;
  cameraPermissionAlert: CameraPermissionAlert;
  faceStatusTexts?: FaceStatusTexts;
  previewScaleType?: PreviewScaleType;
  showCountdownLabel?: boolean;
  showProgressBar?: boolean;
  showFaceStatusLabel?: boolean;
  countdownLabelText?: string;
}

/*
    CameraPermissionAlert is the model for executing the camera permission for the Biometric Capture module
   */
export interface CameraPermissionAlert {
  title: string;
  message: string;
  settingText: string;
  cancelText: string;
}

export interface ErrorResult {
  errorDescription: string;
}
