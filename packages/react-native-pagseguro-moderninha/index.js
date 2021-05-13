"use strict";
exports.__esModule = true;
var react_native_1 = require("react-native");
var RNModerninha = react_native_1.NativeModules.RNModerninha;
var defaultModule = RNModerninha ? require('./src/main') : require('./src/mockup');
module.exports = defaultModule;
exports["default"] = defaultModule;
