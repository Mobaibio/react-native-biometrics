import { requireNativeComponent, UIManager, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'mobai-biometric' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ComponentName = 'RCTMBCaptureSessionView';

interface RCTMobaiCaptureProps {
  ref: React.RefObject<React.Component>;
  onChange: (event: any) => void;
}

export const RCTMobaiCapture =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<RCTMobaiCaptureProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
