import React, { useState, useEffect } from 'react';
import { Button, StyleSheet, Text, ToastAndroid, View } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import RNModerninha from "./packages/react-native-pagseguro-moderninha"

export default function App() {
  const [serialNumber, setSerialNumber] = useState<string>("loading...")

  useEffect(() => {
    (async () => {
      const terminalSerialNumber = await RNModerninha.getTerminalSerialNumber()
      setSerialNumber(terminalSerialNumber)
    })()
  }, [serialNumber])

  const onPrint = () => {
    if (RNModerninha.isModerninha) {
      console.log("vou tentar imprimir algo aqui")
    } else {
      ToastAndroid.show("We're not in Moderninha", ToastAndroid.SHORT);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>React Native Moderninha</Text>
      <Text>isModerninha: { RNModerninha.isModerninha.toString() }</Text>
      <Text>terminalSerialNumber: { serialNumber || "null" }</Text>

      <View style={styles.footer}>
        <Button
          onPress={onPrint}
          title="Print Expo Logo"
          color="black"
          accessibilityLabel="Learn more about this purple button"
        />
      </View>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  footer: {
    marginTop: 20
  },
  title: {
    fontSize: 20,
    marginBottom: 15
  }
});
