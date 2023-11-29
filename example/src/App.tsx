import * as React from 'react';
import { useState, useEffect } from 'react';
import {
  StyleSheet,
  View,
  PermissionsAndroid,
  Platform,
  Alert,
} from 'react-native';
import CaptureScreen from './CaptureScreen';
import MainScreen from './MainScreen';
import { ResultScreen } from './ResultScreen';
import { launchNativeModule } from './NativeModule';

export const enum ScreenRoute {
  MainScreen,
  CaptureScreen,
  ResultScreen,
}
const PERMISSION_GRANTED = 'granted';
const PERMISSION_NOT_GRANTED = 'notGranted';

const requestCameraPermission = async (
  cb: (permissionState: string) => void
) => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.CAMERA
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('You can use the camera');
      cb(PERMISSION_GRANTED);
    } else {
      console.log('Camera permission denied');
      cb(PERMISSION_NOT_GRANTED);
    }
  } catch (err) {
    console.warn(err);
  }
};

const rationaleAlert = (okPressed: () => void) => {
  Alert.alert(
    'MobaiBiometric camera permission',
    'MobaiBiometric needs access to your camera for taking a selfie',
    [
      {
        text: 'Cancel',
        onPress: () => {},
      },
      {
        text: 'Ok',
        onPress: () => {
          okPressed();
        },
      },
    ]
  );
};

export default function App() {
  const [isPermissionGranted, setIspermissionGranted] = useState('');

  const [screenRoute, setScreenRoute] = useState(ScreenRoute.MainScreen);
  const setScreenRouteHandler = () => {
    console.log('Button pressed');
    setScreenRoute(ScreenRoute.CaptureScreen);
  };

  const mainScreen = (
    <MainScreen
      onStartNativeComponent={setScreenRouteHandler}
      onStartNativeModule={launchNativeModule}
    />
  );
  const [resultFrames, setResultFrames] = useState(['']);

  const [currentScreen, setCurrentScreen] = useState(mainScreen);
  useEffect(() => {
    switch (screenRoute) {
      case ScreenRoute.MainScreen:
        setCurrentScreen(mainScreen);
        break;
      case ScreenRoute.ResultScreen:
        setCurrentScreen(<ResultScreen frames={resultFrames} />);
        break;
      case ScreenRoute.CaptureScreen:
        if (Platform.OS === 'android') {
          requestCameraPermission((permissionState) => {
            setIspermissionGranted(permissionState);
          });
        } else if (Platform.OS === 'ios') {
          // Request Camera permission for iOS here then
          // call the function setIspermissionGranted passing a boolean wether permission is granted or not.
          setCurrentScreen(
            <CaptureScreen onFaceCaptureSuccess={handlerFaceCaptureSuccess} />
          );
        }
        break;
    }
  }, [screenRoute]);

  useEffect(() => {
    if (isPermissionGranted === PERMISSION_GRANTED) {
      setCurrentScreen(
        <CaptureScreen onFaceCaptureSuccess={handlerFaceCaptureSuccess} />
      );
    } else if (isPermissionGranted === PERMISSION_NOT_GRANTED) {
      rationaleAlert(() => {
        requestCameraPermission((permissionState) => {
          setIspermissionGranted(permissionState);
        });
      });
    }
  }, [isPermissionGranted]);

  const handlerFaceCaptureSuccess = (frames: [string]) => {
    //console.log(frames.length);
    setResultFrames(frames);
    setScreenRoute(ScreenRoute.ResultScreen);
  };

  return currentScreen;
}

const styles = StyleSheet.create({
  screenContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  overlay: {
    width: '100%',
    height: '100%',
    position: 'absolute',
  },
  statusText: {
    color: 'white',
    fontSize: 25,
    width: '100%',
    paddingStart: 20,
    paddingTop: 10,
  },
});
