import { MobaiBiometric } from './NativeModule';

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
  });
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


interface MBCaptureSessionResult { 
  image: string; 
  padData: string;
}
