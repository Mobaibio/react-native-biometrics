import * as React from 'react';
import { StyleSheet, View, Button } from 'react-native';

export default function MainScreen(props: any) {
  return (
    <View style={styles.container}>
      <Button
        onPress={() => props.onStartNativeComponent()}
        title="Start Native Component"
      />
      <Button
        onPress={() => props.onStartNativeModule()}
        title="Start Native Module"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    marginHorizontal: 16,
  },
});
