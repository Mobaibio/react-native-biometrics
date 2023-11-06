import {
  MBCaptureSessionView,
  FaceStatus,
  setCaptureSessionOptions,
  type MBCaptureSessionResult,
} from 'mobai-biometric';
import React from 'react';
import { useState, useEffect } from 'react';
import { PixelRatio, StyleSheet, View, Text } from 'react-native';
import * as Progress from 'react-native-progress';

export default function CaptureScreen(props: any) {
  function onFaceValidating(faceStatus: FaceStatus) {
    setFaceStatus(faceStatus);
    setCaptureStatus('Finding Face');
    setDownCounter('');
    console.log('Face Validating: ', faceStatus);
  }

  function onFaceCaptureStarted() {
    console.log('Face Capture Started');
    setCaptureStatus('Capture Started');
    setDownCounter('');
  }
  function onFaceCaptureSuccess(
    mbCaptureSessionResult: MBCaptureSessionResult
  ) {
    console.log('Face Capture Success', mbCaptureSessionResult.padData.length);
    setCaptureStatus('Capture Succeded');
    props.onFaceCaptureSuccess(mbCaptureSessionResult);
  }
  function onFaceCaptureProgress(captureProgress: number) {
    setProgress(captureProgress);
    console.log('Face Capture Progress');
    console.log(captureProgress);
  }

  function onFaceCaptureFailed(message: string) {
    console.log('Face Capture Failed');
    setCaptureStatus('Capture Failed');
    setProgress(0);
  }

  function onFaceCaptureCountDown(downCounter: string) {
    setCaptureStatus('Count down before capture');
    setDownCounter(downCounter);
  }

  const [faceStatus, setFaceStatus] = useState('');
  const [captureStatus, setCaptureStatus] = useState('');
  const [progress, setProgress] = useState(0);
  const [downCounter, setDownCounter] = useState('');
  const options = setCaptureSessionOptions({
    numberOfFrameToCollect: 3,
    timeBeforeAutomaticCapture: 3,
  });

  return (
    <View style={styles.screenContainer}>
      <MBCaptureSessionView
        options={options}
        onFaceValidating={onFaceValidating}
        onFaceCaptureStarted={onFaceCaptureStarted}
        onFaceCaptureProgress={onFaceCaptureProgress}
        onFaceCaptureSuccess={onFaceCaptureSuccess}
        onFaceCaptureFailed={onFaceCaptureFailed}
        onFaceCaptureCountDown={onFaceCaptureCountDown}
        style={styles.captureSessionView}
      ></MBCaptureSessionView>
      <Text style={styles.faceStatusText}>{faceStatus}</Text>
      <Text style={styles.captureStatusText}>{captureStatus}</Text>
      <Text style={styles.counterStyle}>{downCounter}</Text>
      <Progress.Bar
        progress={progress}
        style={styles.progressSettings}
        height={80}
        width={200}
        animated={true}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  captureSessionView: {
    marginBottom: 20,
    marginTop: 20,
    marginLeft: 20,
    marginRight: 20,
    flex: 1,
  },

  screenContainer: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'stretch',
  },
  overlay: {
    left: 0,
    top: 0,
    width: '100%',
    height: '100%',
    flexDirection: 'column',
    backgroundColor: 'transparent',
  },
  faceStatusText: {
    color: 'white',
    fontSize: 25,
    position: 'absolute',
    alignSelf: 'center',
    bottom: 200,
  },
  captureStatusText: {
    color: 'white',
    fontSize: 25,
    position: 'absolute',
    alignSelf: 'center',
    top: 200,
  },
  progressSettings: {
    position: 'absolute',
    alignSelf: 'center',
    bottom: 110,
  },

  counterStyle: {
    position: 'absolute',
    alignSelf: 'center',
    top: 230,
    justifyContent: 'center',
    color: 'white',
    fontSize: 25,
  },
});
