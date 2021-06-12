package com.mrrhak.sunmi_thermal_printer;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class SunmiThermalPrinterPlugin implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;
  private static SunmiThermalPrinterModule sunmiThermalPrinterModule;

  private String RESET = "reset";
  private String START_PRINT = "startPrint";
  private String STOP_PRINT = "stopPrint";
  private String IS_PRINTING = "isPrinting";
  private String BOLD_ON = "boldOn";
  private String BOLD_OFF = "boldOff";
  private String UNDERLINE_ON = "underlineOn";
  private String UNDERLINE_OFF = "underlineOff";
  private String EMPTY_LINES = "emptyLines";
  private String PRINT_TEXT = "printText";
  private String PRINT_ROW = "printRow";
  private String PRINT_IMAGE = "printImage";
  private String CUT_PAPER = "cutPaper";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "sunmi_thermal_printer");
    channel.setMethodCallHandler(this);
    sunmiThermalPrinterModule = new SunmiThermalPrinterModule();
    sunmiThermalPrinterModule.initAidl(flutterPluginBinding.getApplicationContext());
  }

  // This static function is optional and equivalent to onAttachedToEngine. It
  // supports the old pre-Flutter-1.12 Android projects. You are encouraged to
  // continue supporting plugin registration via this function while apps migrate
  // to use the new Android APIs post-flutter-1.12 via
  // https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith
  // to keep them functionally equivalent. Only one of onAttachedToEngine or
  // registerWith will be called depending on the user's project.
  // onAttachedToEngine or registerWith must both be defined in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "Sunmi Thermal Printer");
    channel.setMethodCallHandler(new SunmiThermalPrinterPlugin());
    sunmiThermalPrinterModule = new SunmiThermalPrinterModule();
    sunmiThermalPrinterModule.initAidl(registrar.context());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals(RESET)) {
      sunmiThermalPrinterModule.reset();
      result.success(null);
    } else if (call.method.equals(START_PRINT)) {
      sunmiThermalPrinterModule.startPrint();
      result.success(null);
    } else if (call.method.equals(STOP_PRINT)) {
      sunmiThermalPrinterModule.stopPrint();
      result.success(null);
    } else if (call.method.equals(IS_PRINTING)) {
      result.success(sunmiThermalPrinterModule.isPrinting());
    } else if (call.method.equals(BOLD_ON)) {
      sunmiThermalPrinterModule.boldOn();
      result.success(null);
    } else if (call.method.equals(BOLD_OFF)) {
      sunmiThermalPrinterModule.boldOff();
      result.success(null);
    } else if (call.method.equals(UNDERLINE_ON)) {
      sunmiThermalPrinterModule.underlineOn();
      result.success(null);
    } else if (call.method.equals(UNDERLINE_OFF)) {
      sunmiThermalPrinterModule.underlineOff();
      result.success(null);
    } else if (call.method.equals(PRINT_TEXT)) {
      String text = call.argument("text");
      int align = call.argument("align");
      boolean bold = call.argument("bold");
      boolean underline = call.argument("underline");
      int linesAfter = call.argument("linesAfter");
      int size = call.argument("size");
      sunmiThermalPrinterModule.text(text, align, bold, underline, size, linesAfter);
      result.success(null);
    } else if (call.method.equals(EMPTY_LINES)) {
      int n = call.argument("n");
      sunmiThermalPrinterModule.emptyLines(n);
      result.success(null);
    } else if (call.method.equals(PRINT_ROW)) {
      String cols = call.argument("cols");
      boolean bold = call.argument("bold");
      boolean underline = call.argument("underline");
      int textSize = call.argument("textSize");
      int linesAfter = call.argument("linesAfter");
      sunmiThermalPrinterModule.row(cols, bold, underline, textSize, linesAfter);
      result.success(null);
    } else if (call.method.equals(PRINT_IMAGE)) {
      String base64 = call.argument("base64");
      int align = call.argument("align");
      sunmiThermalPrinterModule.printImage(base64, align);
      result.success(null);
    } else if (call.method.equals(CUT_PAPER)) {
      sunmiThermalPrinterModule.cutPaper();
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
