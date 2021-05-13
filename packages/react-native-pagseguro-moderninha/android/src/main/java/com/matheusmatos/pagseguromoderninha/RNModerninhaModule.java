package com.matheusmatos.pagseguromoderninha;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.pranavpandey.android.dynamic.utils.DynamicUnitUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAbortResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrintResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrinterData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrinterListener;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;

public class RNModerninhaModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;
  private static PlugPagAppIdentification appIdentification;
  private static PlugPag plugPag;
  private final String TAG = "RNModerninhaModule";

  public RNModerninhaModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    appIdentification = new PlugPagAppIdentification("ClubTicket", "0.1.0");
    plugPag = new PlugPag(reactContext, appIdentification);
  }

  @Override
  @NotNull
  public String getName() {
    return "RNModerninha";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("TYPE_CREDITO", PlugPag.TYPE_CREDITO);
    constants.put("TYPE_DEBITO", PlugPag.TYPE_DEBITO);
    constants.put("TYPE_VOUCHER", PlugPag.TYPE_VOUCHER);
    constants.put("INSTALLMENT_TYPE_A_VISTA", PlugPag.INSTALLMENT_TYPE_A_VISTA);
    constants.put("INSTALLMENT_TYPE_PARC_COMPRADOR", PlugPag.INSTALLMENT_TYPE_PARC_COMPRADOR);
    constants.put("INSTALLMENT_TYPE_PARC_VENDEDOR", PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR);
    return constants;
  }

  private void sendEvent(@Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onPlugPagEvent", params);
  }

  @ReactMethod
  public void getLibVersion(Callback callback) {
    String version = plugPag.getLibVersion();
    callback.invoke(version);
  }

  @ReactMethod
  public void isAuthenticated(Callback callback) {
    Boolean isAuthenticated = plugPag.isAuthenticated();
    callback.invoke(isAuthenticated);
  }

  @ReactMethod
  public void initializeAndActivatePinPad(String activationCode, Callback successCallback, Callback errorCallback) {
    if (plugPag.isAuthenticated()) {
      errorCallback.invoke("10", "Pinpad already activated.");
    } else {
      PlugPagActivationData plugPagActivationData = new PlugPagActivationData(activationCode);
      PlugPagInitializationResult result = plugPag.initializeAndActivatePinpad(plugPagActivationData);

      if(result.getResult() == PlugPag.RET_OK) {
        successCallback.invoke(result.getErrorCode(), result.getErrorMessage());
      } else {
        errorCallback.invoke(result.getErrorCode(), result.getErrorMessage());
      }
    }
  }

  @ReactMethod
  public void doPayment(ReadableMap options) {

    plugPag.setEventListener((@NotNull PlugPagEventData plugPagEventData) -> {
      WritableMap params = Arguments.createMap();
      params.putString("eventType", "process");
      params.putInt("code", plugPagEventData.getEventCode());
      params.putString("message", plugPagEventData.getCustomMessage());
      sendEvent(params);
    });

    WritableMap params = Arguments.createMap();
    params.putInt("code", 1);
    params.putString("message", "INICIANDO");
    sendEvent(params);

    PlugPagPaymentData paymentData =
            new PlugPagPaymentData(
                    options.getInt("type"),
                    options.getInt("amount"),
                    options.getInt("installmentType"),
                    options.getInt("installments"),
                    options.getString("userReference"));

    Thread paymentThread = new Thread(() -> {
      PlugPagTransactionResult result = plugPag.doPayment(paymentData);

      WritableMap payload = Arguments.createMap();
      payload.putInt("code", result.getResult());
      payload.putString("message", result.getMessage());
      payload.putString("cardApplication", result.getCardApplication());
      payload.putString("bin", result.getBin());
      payload.putString("availableBalance", result.getAvailableBalance());
      payload.putString("amount", result.getAmount());
      payload.putString("cardBrand", result.getCardBrand());
      payload.putString("cardCryptogram", result.getCardCryptogram());
      payload.putString("date", result.getDate());
      payload.putString("errorCode", result.getErrorCode());
      payload.putString("extendedHolderName", result.getExtendedHolderName());
      payload.putString("holder", result.getHolder());
      payload.putString("hostNsu", result.getHostNsu());
      payload.putString("label", result.getLabel());
      payload.putString("terminalSerialNumber", result.getTerminalSerialNumber());
      payload.putString("transactionCode", result.getTransactionCode());
      payload.putString("transactionId", result.getTransactionId());
      payload.putString("eventType", "result");
      sendEvent(payload);
    });

    paymentThread.start();
  }

  @ReactMethod
  public void getTerminalSerialNumber(Callback callback) {
    String string = Build.SERIAL;
    callback.invoke(string);
  }

  @ReactMethod
  public void abort(Callback callback) {
    PlugPagAbortResult result = plugPag.abort();
    int code = result.getResult();
    callback.invoke(code);
  }

  private @NonNull static Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
    if (width > 0 && height > 0) {
      view.measure(View.MeasureSpec.makeMeasureSpec(DynamicUnitUtils
                      .convertDpToPixels(width), View.MeasureSpec.EXACTLY),
              View.MeasureSpec.makeMeasureSpec(DynamicUnitUtils
                      .convertDpToPixels(height), View.MeasureSpec.EXACTLY));
    }
    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
            view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Drawable background = view.getBackground();

    if (background != null) {
      background.draw(canvas);
    }
    view.draw(canvas);

    return bitmap;
  }

  @ReactMethod
  public void print(String uri, Callback successCallback, Callback errorCallback) {
    PlugPagPrinterData data = new PlugPagPrinterData(
            Environment.getExternalStorageDirectory().getAbsolutePath() + uri,
            4,
            10 * 12);

    PlugPagPrinterListener listener = new PlugPagPrinterListener() {
      @Override
      public void onError(@NotNull PlugPagPrintResult plugPagPrintResult) {
        String message = plugPagPrintResult.getMessage();
        String code = plugPagPrintResult.getErrorCode();
        Log.d("PRINTER", "message: " + message + "; code = " + code);
        errorCallback.invoke(code, message);
      }

      @Override
      public void onSuccess(@NotNull PlugPagPrintResult plugPagPrintResult) {
        String message = plugPagPrintResult.getMessage();
        String code = plugPagPrintResult.getErrorCode();
        Log.d("PRINTER", "message: " + message + "; code = " + code);
        successCallback.invoke(code, message);
      }
    };

    plugPag.setPrinterListener(listener);

    PlugPagPrintResult result = plugPag.printFromFile(data);

    if (result.getResult() != 0) {
      Log.d("PRINTER", "IF SUCESSO!!");
    }
  }
}