import * as React from 'react';

import { StyleSheet, View, Button, NativeEventEmitter } from 'react-native';
import { launch, MobaiBiometric } from 'mobai-biometric';

const mobaiBiometricEmmiter = new NativeEventEmitter(MobaiBiometric);

export default function App() {
  const onCaptureFinished = (event: any) => {
    console.log('onCaptureFinished:' + event);
  };

  const onFailureWithErrorMessage = (event: any) => {
    console.log('onFailureWithErrorMessage:' + event);
  };

  mobaiBiometricEmmiter.addListener('onCaptureFinished', onCaptureFinished);

  mobaiBiometricEmmiter.addListener(
    'onFailureWithErrorMessage',
    onFailureWithErrorMessage
  );

  const onPress = () => {
    launch();
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
  },
});
