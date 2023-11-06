import React from 'react';
import { UIManager, findNodeHandle, Platform } from 'react-native';
import { RCTMobaiCapture } from './NativeComponent';

/*
    MBCaptureSessionResult is the Biometric Capture module's result model
   */

export interface MBCaptureSessionResult {
  image: string;
  padData: [string];
}

/*
        ErrorResult is the Biometric Capture module capture's error model
       */
export interface MBCaptureSessionError {
  errorDescription: string;
}

/*
      RCTMobaiCapture is the direct instance Biometric capture component as a view 
    */

/*
      PropsOutput is the model for event handling from the component to the caller
    */
interface PropsOutput {
  options: MBCaptureSessionOptions;
  onFaceValidating: (faceStatus: FaceStatus) => void;
  onFaceCaptureStarted: () => void;
  onFaceCaptureSuccess: (
    mbCaptureSessionResult: MBCaptureSessionResult
  ) => void;
  onFaceCaptureProgress: (captureProgress: number) => void;
  onFaceCaptureFailed: (message: string) => void;
  onFaceCaptureCountDown: (downCounter: string) => void;
}

/*
      FaceStatus is the face status at every moment while the capture session is running
    */
export const enum FaceStatus {
  TooFarAway = 'TooFarAway',
  TooFarUp = 'TooFarUp',
  TooFarDown = 'TooFarDown',
  TooFarLeft = 'TooFarLeft',
  TooFarRight = 'TooFarRight',
  TooClose = 'TooClose',
  NotFound = 'NotFound',
  TooManyFaces = 'TooManyFaces',
  ValidFace = 'ValidFace',
}

/*
      MobaiBiometricComponentOptions is the base options for the capture session as a component.
    */
interface MBCaptureSessionOptions {
  numberOfFramesBeforeCapture?: number;
  numberOfFrameToCollect?: number;
  frameInterval?: number;
  timeBeforeAutomaticCapture?: number;
  isDebugging?: boolean;
}

/*
        setCaptureSessionOptions initialize MobaiBiometricOptions with the caller options input.
       */
export function setCaptureSessionOptions(options: MBCaptureSessionOptions) {
  return {
    numberOfFramesBeforeCapture: options.numberOfFramesBeforeCapture,
    numberOfFrameToCollect: options.numberOfFrameToCollect,
    frameInterval: options.frameInterval,
    timeBeforeAutomaticCapture: options.timeBeforeAutomaticCapture,
    isDebugging: options.isDebugging,
  };
}

/*
      createFragment creates the fragment container of the camera view for the native android component
     */
const createFragment = (viewId: any) =>
  UIManager.dispatchViewManagerCommand(viewId, '1', [viewId]);

export class MBCaptureSessionView extends React.Component<PropsOutput> {
  constructor(props) {
    super(props);
    this._onChange = this._onChange.bind(this);
    //this._onFaceValidating = this._onFaceValidating.bind(this);
    this.ref = React.createRef();
  }

  componentDidMount() {
    if (Platform.OS === 'android') {
      const viewId = findNodeHandle(this.ref.current);
      createFragment(viewId);
    }
  }

  _onChange = (event) => {
    if (event.nativeEvent.messageFaceStatus !== undefined) {
      switch (event.nativeEvent.messageFaceStatus) {
        case 'TOO_FAR_AWAY':
          this.props.onFaceValidating(FaceStatus.TooFarAway);
          break;
        case 'TOO_CLOSE':
          this.props.onFaceValidating(FaceStatus.TooClose);
          break;
        case 'TOO_FAR_DOWN':
          this.props.onFaceValidating(FaceStatus.TooFarDown);
          break;
        case 'TOO_FAR_LEFT':
          this.props.onFaceValidating(FaceStatus.TooFarLeft);
          break;
        case 'TOO_FAR_RIGHT':
          this.props.onFaceValidating(FaceStatus.TooFarRight);
          break;
        case 'NOT_FOUND':
          this.props.onFaceValidating(FaceStatus.NotFound);
          break;
        case 'TOO_MANY':
          this.props.onFaceValidating(FaceStatus.TooManyFaces);
          break;
        case 'VALID':
          this.props.onFaceValidating(FaceStatus.ValidFace);
          break;
        case 'TOO_FAR_UP':
          this.props.onFaceValidating(FaceStatus.TooFarUp);
          break;
        default:
      }
    }

    if (event.nativeEvent.messageCaptureStarted !== undefined) {
      // sends the capture started event to the caller.
      this.props.onFaceCaptureStarted();
      console.log(event.nativeEvent.messageCaptureStarted);
    }

    if (event.nativeEvent.messageCaptureFinished !== undefined) {
      this.props.onFaceCaptureSuccess(event.nativeEvent.messageCaptureFinished);
    }

    if (event.nativeEvent.messageCaptureProgress !== undefined) {
      console.log(
        'CaptureProgress: ' +
          event.nativeEvent.messageCaptureProgress.toString()
      );
      this.props.onFaceCaptureProgress(
        +event.nativeEvent.messageCaptureProgress
      );
    }

    if (event.nativeEvent.massageCaptureFailed !== undefined) {
      console.log(event.nativeEvent.massageCaptureFailed);
      this.props.onFaceCaptureFailed(event.nativeEvent.massageCaptureFailed);
    }

    if (event.nativeEvent.messageCaptureCountDown !== undefined) {
      console.log(event.nativeEvent.messageCaptureCountDown);
      this.props.onFaceCaptureCountDown(
        event.nativeEvent.messageCaptureCountDown
      );
    }
  };
  render() {
    return (
      <RCTMobaiCapture
        {...this.props}
        ref={this.ref}
        onChange={this._onChange}
      />
    );
  }
}
