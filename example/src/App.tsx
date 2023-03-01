import * as React from 'react';

import { StyleSheet, View, Button, NativeEventEmitter } from 'react-native';
import {
  ErrorResult,
  MBCaptureSessionResult,
  EVENT_FAILURE,
  EVENT_SUCCESS,
  launch,
  MobaiBiometric,
  MobaiBiometricOptions,
} from 'mobai-biometric';

const mobaiBiometricEmmiter = new NativeEventEmitter(MobaiBiometric);

export default function App() {
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

  const onPress = () => {
    var myOptions: MobaiBiometricOptions = { autoCaptureEnabled: true };
    launch(myOptions);
  };

  return (
    <View style={styles.screenContainer}>
      <Button title="Click here to start capture session." onPress={onPress} />
    </View>
  );
}

const styles = StyleSheet.create({
  screenContainer: {
    flex: 1,
    justifyContent: 'center',
    padding: 16,
  },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
    backgroundColor: 'blue',
    position: 'absolute',
    zIndex: 999,
  },
});
