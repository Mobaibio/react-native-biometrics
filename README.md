React Native version of Mobai Biometric mobile SDK

## Installation

1. Use the following command to add the dependency to your package.json file:

   ```console
   npm i @mobaibio/mobai-biometric
   ```

1. Run the following command to install the dependency

   ```console
   yarn
   ```

### iOS

1. The SDK needs to access the camera on the device. A `NSCameraUsageDescription` must be added to the Info.plist for your application.
2. Some additions must be added to the podfile for the iOS project in order to integrate the SDK correctly.

   - If `use_frameworks! :linkage => :static` is used then the following must be added to the podfile to ensure dependency is built as a dynamic framework:

     ```ruby
     pre_install do |installer|
         installer.pod_targets.each do |pod|
         if ['SwiftProtobuf'].include?(pod.name)
             def pod.dynamic_framework?;
                 true
             end
             def pod.build_type;
                 Pod::BuildType.dynamic_framework
             end
         end
         end
     end
     ```

   - A post install script to enable module stability in the SDK and its dependencies:

     ```ruby
     post_install do |installer|
         installer.pods_project.targets.each do |target|
             if ['MobaiBiometric', 'SwiftProtobuf'].include? target.name
                 target.build_configurations.each do |config|
                 config.build_settings['BUILD_LIBRARY_FOR_DISTRIBUTION'] = 'YES'
             end
         end
     end
     ```

### Android

The repository where the MobaiBiometric SDK used by the React Native module is located must be added to the list of repositories.

```groovy
// build.gradle (Project)
allprojects {
    repositories {
        maven {
            url "https://gitlab.com/api/v4/projects/36441060/packages/maven"
        }
    }
}
```

## Usage

In order to start the screen in React Native first you can call `launch` method that can be imported from the `@mobaibio/mobai-biometric` package

```js
import {
  MobaiBiometric,
  launch
  };
    const onPress = () => {
    var myOptions: MobaiBiometricOptions = { };
    launch(myOptions);
  };
```

### Options

Inside the library we have some options for changing behaviour of capturing data:
| Variable Name | Type | Default Value |
| --------------------------- | ---------------------| ------------- |
| numberOfFramesBeforeCapture | number | 10 |
| numberOfFrameToCollect | number | 3 |
| frameInterval | number | 10 |
| timeBeforeAutomaticCapture | number | 4 |
| isDebugging | boolean | false |
| cameraPermissionAlert | CameraPermissionAlert| undefined |
| faceStatusTexts | FaceStatusTexts | undefined |
| previewScaleType | PreviewScaleType | fill |
| showCountdownLabel | boolean | false |
| countdownLabelText | string | 'Hold Still' |
| showProgressBar | boolean | false |
| showFaceStatusLabel | boolean | false |

- `numberOfFramesBeforeCapture`

  - describes number of frame that library capture before starting the process of capturing.

- `numberOfFrameToCollect`

  - describes the number of frames to collect during the capture session.

- `frameInterval`

  - after collected the first frame, is the number of frames to skip before collecting a frame.

- `timeBeforeAutomaticCapture`

  - number of seconds that user needs to wait in automatic capture.

- `cameraPermissionAler`t - Camera permission object for showing alert when permission is denied from user

  - `title` - title of the alert
  - `message` - message of the alert
  - `settingText` - title for the button to go to setting menu
  - `cancelText` - title for cancel

- `isDebugging`

  - If it is set to false will just display the overlay on the top of the camera.
  - If it is set to true will display all components available ont the top of the camera(timer text, progress bar and face status text).

- `faceStatusTexts`

  - Is used to customize or localize the text displayed to the user indicating if the face is in correct position or not.
    if no input is provided to `faceStatusTexts` default values are used.

- `previewScaleType`

  - The mode used to scale the camera preview in the view. It can be set to either `fit` or `fill`. `PreviewScaleType.fill` will ensure the camera preview is taking up the whole view in both width and height. However, this means that some parts of the image will be cropped from the preview. This does not have any impact on the images captured from the camera. `PreviewScaleType.fit` will ensure that exactly what is captured from the camera is shown in the view.

- `showCountdownLabel`

  - Is used to show or hide the label displayed during countdown before capture starts.
  - The text for the countdownLabel can be set using the `countdownLabelText` option.

- `countdownLabelText`

  - Is used to customize the text shown in the countdown label

- `showProgressBar`

  - Is used to show or hide the progress bar. The progress bar indicates to the user how far in the capture process we are.

- `showFaceStatusLabel`

  - Is used to show or hide the face status label. The face status label is used to indicate to the user if the face is in the correct position or not.

### Props objects

`FaceStatusTexts`

- Object containing the face status strings

| Member          | Type   | Description                                                        |
| --------------- | ------ | ------------------------------------------------------------------ |
| faceTooFarAway  | string | Text to display when the face is too far away from the camera      |
| faceTooFarUp    | string | Text to display when the face i too far up in the camera           |
| faceTooFarDown  | string | Text to display when the face is too far down in the camera        |
| faceTooFarLeft  | string | Text to display when the face is too far left in the camera        |
| faceTooFarRight | string | Text to display when the face is too far right in the camera       |
| faceTooClose    | string | Text to display when the face is too close to the camera           |
| faceNotFound    | string | Text to display when no face is found                              |
| tooManyFaces    | string | Text to display when too many faces are found in the camera        |
| validFace       | string | Text to display when the face is in a valid position in the camera |

### Event Emitters

In order to send data back from the library we are using event emitters:

| Event Name    | Event Type             |
| ------------- | ---------------------- |
| EVENT_SUCCESS | MBCaptureSessionResult |
| EVENT_FAILURE | ErrorResult            |

### Example Class

Here is an example class in React Native

```js
import {
  ErrorResult,
  MBCaptureSessionResult,
  EVENT_FAILURE,
  EVENT_SUCCESS,
  launch,
  MobaiBiometric,
  MobaiBiometricOptions,
} from 'mobai-biometric';

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
```

# React Native Component for Mobai Biometric SDK

### ` MBCaptureSessionView`

` MBCaptureSessionView` usage example.

```js
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
```

Importing block

```js
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
```

Return block

```js
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
```

### Functions

The function `onFaceValidating` handles events when the face is validating. For instance updating the face status text view value. The function returns an instance of `FaceStatus`

```js
function onFaceValidating(faceStatus: FaceStatus) {
  setFaceStatus(faceStatus);
  setCaptureStatus('Finding Face');
  setDownCounter('');
  console.log('Face Validating: ', faceStatus);
}
const [faceStatus, setFaceStatus] = useState('');
```

`FaceStatus` represents the current face status-position in the camera preview.
Face status | Description
-------------|-------------
TooFarAway | The face is too far away from the camera
TooFarUp | The face is too far up from the camera
TooFarDown | The face is too far down from the camera
TooFarLeft | The face is too far left from the camera
TooFarRight | The face is too far right from the camera
TooClose | The face is too close to the camera
NotFound | There is not a face in the camera
TooManyFaces | There are too many faces in the camera
ValidFace | The face is in the correct position

The function `onFaceCaptureStarted` is executed when the capture session has started

```js
function onFaceCaptureStarted() {
  console.log('Face Capture Started');
  setCaptureStatus('Capture Started');
  setDownCounter('');
}
```

The function `onFaceCaptureSuccess` is executed when the capture session has successfully finished. It returns a `MBCaptureSessionResult`, an object that contains a collection of frames and the base frame, all of them in StringBase64 format.

```js
function onFaceCaptureSuccess(mbCaptureSessionResult: MBCaptureSessionResult) {
  console.log('Face Capture Success', mbCaptureSessionResult.padData.length);
  setCaptureStatus('Capture Succeded');
  props.onFaceCaptureSuccess(mbCaptureSessionResult);
}
```

The function `onFaceCaptureProgress` update the face capture progress value. It returns a number representing the progress with values from 0-1, specially useful for updating a progress bar for instance.

```js
function onFaceCaptureProgress(captureProgress: number) {
  setProgress(captureProgress);
  console.log('Face Capture Progress');
  console.log(captureProgress);
}
```

The function `onFaceCaptureFailed` is executed when the capture session fails. It returns a string describing the reason of the failure.

```js
function onFaceCaptureFailed(message: string) {
  console.log('Face Capture Failed');
  setCaptureStatus('Capture Failed');
  setProgress(0);
}
```

The function `onFaceCaptureCountDown` is executed every second after a face is valid until the capture session start collecting frames.

```js
function onFaceCaptureCountDown(downCounter: string) {
  setCaptureStatus('Count down before capture');
  setDownCounter(downCounter);
}
```

### Hooks

Hook that handle the face status text view

```js
const [faceStatus, setFaceStatus] = useState('');
```

Hook tha handle the capture status text view ot showing in which stage the capture process is.

```js
const [captureStatus, setCaptureStatus] = useState('');
```

Hook that handle-update the progressbar value

```js
const [progress, setProgress] = useState(0);
```

Hook that handle the count down text view

```js
const [downCounter, setDownCounter] = useState('');
```

### Components

Text view that displays the face status on the top of the camera preview

```js
<Text style={styles.faceStatusText}>{faceStatus}</Text>
```

Text view that displays the capture status

```js
<Text style={styles.captureStatusText}>{captureStatus}</Text>
```

Text view that displays the count down

```js
<Text style={styles.counterStyle}>{downCounter}</Text>
```

Component tha display the progress bar

```js
<Progress.Bar
  progress={progress}
  style={styles.progressSettings}
  height={80}
  width={200}
  animated={true}
/>
```

`MBCaptureSessionView` implementation with its parameters and events. Description is pretty the same as at the above `Hooks` session

```js
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
```

### Options

Props associated with `MBCaptureSessionView`.
Inside the library we have some options for changing behaviour of capturing data:
Capture session options.

- `numberOfFrameToCollect` Describes the number of frames to collect during the capture session.
- `frameInterval` After collecting the first frame, is the number of frames to skip before collecting a frame.
- `numberOfFramesBeforeCapture` Describes the number of frames to skip before it starts collecting.
- `timeBeforeAutomaticCapture` Is the set time in seconds the capture should wait to start collecting frames.
- `previewScaleType` The mode used to scale the camera preview in the view. It can be set to either fit or fill. PreviewScaleType.fill will ensure the camera preview is taking up the whole view in both width and height. However, this means that some parts of the image will be cropped from the preview. This does not have any impact on the images captured from the camera. PreviewScaleType.fit will ensure that exactly what is captured from the camera is shown in the view.

| Variable Name               | Type    | Default Value         |
| --------------------------- | ------- | --------------------- |
| numberOfFramesBeforeCapture | number  | 10                    |
| numberOfFrameToCollect      | number  | 3                     |
| frameInterval               | number  | 10                    |
| timeBeforeAutomaticCapture  | number  | 4                     |
| isDebugging                 | boolean | false                 |
| previewScaleType            | enum    | PreviewScaleType.fill |

Options implementation example:

```js

const options = setCaptureSessionOptions(
  {
    numberOfFrameToCollect: 5,
    numberOfFramesBeforeCapture: 5,
    frameInterval: 5,
    timeBeforeAutomaticCapture: 5,
    previewScaleType: PreviewScaleType.fill,
  }
  )

<MBCaptureSessionView
 options={options}
 style={styles.captureSessionView}
></MBCaptureSessionView>
/>
```

### Events

Events associated with `MBCaptureSessionView`
Event Name | Description | Return type
----------------------|-------------------------------------------------------------------------|----------------
onFaceValidating | Provides the current face status-position in the camera preview | `FaceStatus`
onFaceCaptureStarted | Is execute the capture session starts | `String`
onFaceCaptureSuccess | Is executed when the capture session is successfully finished | `MBCaptureSessionResult`
onFaceCaptureProgress | It shows the capture session progress with a value from 0-1 | `Boolean`
onFaceCaptureFailed | It is executed when the face capture fails, it returns the type of error| `String`
onFaceCaptureCountDown| It returns the remaining time in seconds before start collecting frames | `String`
