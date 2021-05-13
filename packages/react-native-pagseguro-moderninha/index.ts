import { NativeModules } from "react-native"
const { RNModerninha } = NativeModules

export interface PagSeguroModerninha {
    isModerninha: boolean
    TYPE_CREDITO: number
    TYPE_DEBITO: number
    TYPE_VOUCHER: number
    INSTALLMENT_TYPE_A_VISTA: number
    INSTALLMENT_TYPE_PARC_COMPRADOR: number
    INSTALLMENT_TYPE_PARC_VENDEDOR: number

    getTerminalSerialNumber(): Promise<string>
}

const defaultModule: PagSeguroModerninha = RNModerninha ? require('./src/main') : require('./src/mockup')
module.exports = defaultModule
export default defaultModule
