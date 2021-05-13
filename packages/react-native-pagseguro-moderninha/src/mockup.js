
/**
 * This is just a simple mockup when we don't have
 * the native module installed in the client/standalone
 */

export default {
  isModerninha: false,
  serialNumber: null,
  getTerminalSerialNumber: function () {
    return Promise.resolve(null)
  }
}
