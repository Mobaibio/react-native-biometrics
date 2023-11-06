import * as React from 'react';
import { StyleSheet, View,Button} from 'react-native';

export default function MainScreen(props: any) {
    return(
       <View style={styles.container}>
         <Button onPress={() => props.onStartCapture()} title="Start Capture"/>
       </View>
    );
}

const styles = StyleSheet.create({
    container: {
      flex: 1,
      justifyContent: 'center',
      marginHorizontal: 16,
    }
  });

