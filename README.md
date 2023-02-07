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

TBD

## Usage

```js
import { launch, MobaiBiometric } from 'mobai-biometric';

const mobaiBiometricEmmiter = new NativeEventEmitter(MobaiBiometric);

const onCaptureFinished = (event: any) => {
    console.log('onCaptureFinished:' + event);
};

const onFailureWithErrorMessage = (event: any) => {
    console.log('onFailureWithErrorMessage:' + event);
};

mobaiBiometricEmmiter.addListener('onCaptureFinished', onCaptureFinished);

mobaiBiometricEmmiter.addListener('onFailureWithErrorMessage', onFailureWithErrorMessage);
```
