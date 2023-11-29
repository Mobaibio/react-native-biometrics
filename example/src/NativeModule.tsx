import {
  type MBCaptureSessionResult,
  EVENT_FAILURE,
  EVENT_SUCCESS,
  type ErrorResult,
  launch,
  type MobaiBiometricOptions,
  type CameraPermissionAlert,
  type FaceStatusTexts,
  PreviewScaleType,
  MobaiBiometric,
} from 'mobai-biometric';

import { NativeEventEmitter } from 'react-native';

export const mobaiBiometricEmmiter = new NativeEventEmitter(MobaiBiometric);

export const launchNativeModule = () => {
  var cameraPermissionAlert: CameraPermissionAlert = {
    title: 'Enable Camera',
    message: 'Go to settings to enable camera',
    settingText: 'Open Settings',
    cancelText: 'Cancel View',
  };
  var statusTexts: FaceStatusTexts = {
    faceTooFarAway: 'Too Far Away',
    faceTooFarUp: 'Too Far Up',
    faceTooFarDown: 'Too Far Down',
    faceTooFarLeft: 'Too Far Left',
    faceTooFarRight: 'Too Far Right',
    faceTooClose: 'Too Close',
    faceNotFound: 'Face Not Found',
    tooManyFaces: 'Too Many Faces',
    validFace: 'Face Found',
  };
  var myOptions: MobaiBiometricOptions = {
    cameraPermissionAlert: cameraPermissionAlert,
    previewScaleType: PreviewScaleType.fill,
    showProgressBar: true,
    showFaceStatusLabel: true,
    showCountdownLabel: false,
    isDebugging: false,
    faceStatusTexts: statusTexts,
    countdownLabelText: 'Hold Still',
  };
  launch(myOptions);
};

const onCaptureFinished = (result: MBCaptureSessionResult) => {
  console.log(EVENT_SUCCESS + 'onCaptureFinished' + result.image.length);
  if (result.image !== undefined && result.padData !== undefined) {
    console.log(EVENT_SUCCESS + ' image ' + result.image.length);
    console.log(EVENT_SUCCESS + ' padData ' + result.padData.length);
  } else {
    console.log('Object is not comming');
  }
};

const onFailureWithErrorMessage = (result: ErrorResult) => {
  if (result.errorDescription !== undefined) {
    console.log(EVENT_FAILURE + ' error ' + result.errorDescription);
  } else {
    console.log('Object is not comming');
  }
};

mobaiBiometricEmmiter.addListener(EVENT_SUCCESS, onCaptureFinished);

mobaiBiometricEmmiter.addListener(EVENT_FAILURE, onFailureWithErrorMessage);
