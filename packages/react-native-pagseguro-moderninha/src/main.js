import { NativeEventEmitter, NativeModules } from "react-native"
const { RNModerninha } = NativeModules

export default {
  isModerninha: true,
  TYPE_CREDITO: RNModerninha.TYPE_CREDITO,
  TYPE_DEBITO: RNModerninha.TYPE_DEBITO,
  TYPE_VOUCHER: RNModerninha.TYPE_VOUCHER,
  INSTALLMENT_TYPE_A_VISTA: RNModerninha.INSTALLMENT_TYPE_A_VISTA,
  INSTALLMENT_TYPE_PARC_COMPRADOR: RNModerninha.INSTALLMENT_TYPE_PARC_COMPRADOR,
  INSTALLMENT_TYPE_PARC_VENDEDOR: RNModerninha.INSTALLMENT_TYPE_PARC_VENDEDOR,

  getLibVersion: function (callback) {
    return RNModerninha.getLibVersion(version => callback(version))
  },
  isAuthenticated: function (callback) {
    return RNModerninha.isAuthenticated(isAuthenticated => callback(isAuthenticated))
  },
  initializeAndActivatePinPad: function (activationCode) {
    return new Promise((resolve, reject) => {
      RNModerninha.initializeAndActivatePinPad(
        activationCode,
        (code) => resolve(code),
        (code, message) => reject(code, message)
      )
    })
  },
  abort: function (callback) {
    if (!callback) {
      callback = () => console.log("You can implement a callback to abort an operation")
      return RNModerninha.abort(callback)
    }
    return RNModerninha.abort(callback)
  },
  doPayment: function (options) {
    // This method trigger the payment workflow, you must
    // follow the progress using the onPlugPagEvent listener
    RNModerninha.doPayment(options)
  },
  onPlugPagEvent: function (callback) {
    const eventEmitter = new NativeEventEmitter(RNModerninha)
    return eventEmitter.addListener('onPlugPagEvent', callback)
  },
  getTerminalSerialNumber: function () {
    return new Promise(resolve => {
      RNModerninha.getTerminalSerialNumber(serialNumber => {
        resolve(serialNumber)
      })
    })
  }
}
