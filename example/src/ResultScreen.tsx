import * as React from 'react';
import { StyleSheet, View, Text } from 'react-native';

export function ResultScreen(props: any) {
  return (
    <View style={styles.container}>
      <Text style={styles.resultText}>Captured Finished</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    marginHorizontal: 16,
  },
  resultText: {
    color: 'green',
    fontSize: 25,
    position: 'absolute',
    alignSelf: 'center',
    bottom: 200,
  },
});
