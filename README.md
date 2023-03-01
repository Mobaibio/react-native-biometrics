# React Native module for Mobai Biometric SDK

React Native version of Mobai Biometric mobile SDK

## Installation

1. Add the following line to your .npmrc file:

    ```shell
    @mobaibio-public:registry=https://gitlab.com/api/v4/packages/npm/
    ```

2. Use the following command to add the dependecy to your package.json file:

    ```console
    npm i @mobaibio-public/mobai-biometric
    ```

3. Run the following command to install the dependency

    ```console
    yarn 
    ```

### iOS

1. The SDK needs to access the camera on the device. A `NSCameraUsageDescription` must be added to the Info.plist for your application.
2. Some additions must be added to the podfile for the iOS project in order to integrate the SDK correctly.
   - The source to the git repository where the podspec for the `MobaiBiometric` pod is hosted must be added to the podfile. The source to the CocoaPods CDN should also be added to be able to still obtain pods from there:

     ```ruby
     source 'https://cdn.cocoapods.org/'
     source 'https://gitlab.com/mobaibio-public/podspecs.git'
     ```

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
