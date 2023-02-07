import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'mobai-biometric' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export const MobaiBiometric = NativeModules.MobaiReactNative
  ? NativeModules.MobaiReactNative
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function launch(): void {
  MobaiBiometric.launch({
    autoCaptureEnabled: true,
    isDebugging: false,
  });
}
