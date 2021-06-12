package com.mrrhak.sunmi_thermal_printer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.widget.Toast;

import com.sunmi.pay.hardware.aidl.print.PrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.mrrhak.sunmi_thermal_printer.entities.TableItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InnerPrinterUtil {
    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";

    private SunmiPrinterService woyouService;
    private static InnerPrinterUtil mInnerPrinterUtil = new InnerPrinterUtil();
    private Context context;

    private InnerPrinterUtil() {
    }

    public static InnerPrinterUtil getInstance() {
        return mInnerPrinterUtil;
    }

    /**
     * Connect service
     *
     * @param context context
     */
    public void connectPrinterService(Context context) {
        this.context = context.getApplicationContext();
        try {
            boolean result = InnerPrinterManager.getInstance().bindService(this.context,
                    connService);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect service
     *
     * @param context context
     */
    public void disconnectPrinterService(Context context) {
        if (woyouService != null) {
            try {
                InnerPrinterManager.getInstance().unBindService(this.context,
                        connService);
            } catch (InnerPrinterException e) {
                e.printStackTrace();
            }
            woyouService = null;
        }
    }

    public boolean isConnect() {
        return woyouService != null;
    }

    InnerPrinterCallback connService = new InnerPrinterCallback(){
        @Override
        protected void onConnected(SunmiPrinterService service){
            woyouService = service;
        }

        @Override
        protected void onDisconnected() {
            woyouService = null;
        }
    };

    public InnerResultCallbcak generateCB(final PrinterCallback printerCallback) {
        return new InnerResultCallbcak() {
            @Override
            public void onRunResult(boolean isSuccess) {

            }

            @Override
            public void onReturnString(String result) {
            }

            @Override
            public void onRaiseException(int code, String msg) {

            }

            @Override
            public void onPrintResult(int code, String msg) {

            }
        };
    }

    /**
     * Set print density
     */
    private int[] darkness = new int[] { 0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0, 0xffff, 0xfeff, 0xfdff,
            0xfcff, 0xfbff, 0xfaff };

    public void setDarkness(int index) {
        if (woyouService == null) {
            return;
        }

        int k = darkness[index];
        try {
            woyouService.sendRAWData(ESCUtil.setPrinterDarkness(k), null);
            woyouService.printerSelfChecking(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the printer system information and put it in the list
     *
     * @return list
     */
    public List<String> getPrinterInfo(PrinterCallback printerCallback1, PrinterCallback printerCallback2) {
        if (woyouService == null) {
            return null;
        }

        List<String> info = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        try {
            woyouService.getPrintedLength(generateCB(printerCallback1));
            woyouService.getPrinterFactory(generateCB(printerCallback2));
            info.add(woyouService.getPrinterSerialNo());
            info.add(woyouService.getPrinterModal());
            info.add(woyouService.getPrinterVersion());
            PackageInfo packageInfo = packageManager.getPackageInfo(SERVICE＿PACKAGE, 0);
            if (packageInfo != null) {
                info.add(packageInfo.versionName);
                info.add(packageInfo.versionCode + "");
            } else {
                info.add("");
                info.add("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    /**
     * Initialize the printer
     */
    public void initPrinter() {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.printerInit(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setFontSize(int size) {
        try {
            woyouService.setFontSize(size, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print QR code
     */
    public void printQr(String data, int moduleSize, int errorlevel) {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.setAlignment(1, null);
            woyouService.printQRCode(data, moduleSize, errorlevel, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print Barcode
     */
    public void printBarCode(String data, int symbology, int height, int width, int textPosition) {
        if (woyouService == null) {
            return;
        }
        try {
            woyouService.printBarCode(data, symbology, height, width, textPosition, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print Text
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine) {
        if (woyouService == null) {
            return;
        }
        try {
            if (isBold) {
                woyouService.sendRAWData(ESCUtil.boldOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (isUnderLine) {
                woyouService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.underlineOff(), null);
            }

            woyouService.printTextWithFont(content, null, size, null);
            // woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print Picture
     */
    public void printBitmap(Bitmap bitmap, int align) {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.setAlignment(align, null);
            woyouService.printBitmap(bitmap, null);
            woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void lineWrap(int line) {
        try {
            woyouService.lineWrap(line, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print pictures and text in the specified arrangement order
     */
    public void printBitmap2(Bitmap bitmap, int orientation) {
        if (woyouService == null) {
            Toast.makeText(context, "Service has been disconnected!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            if (orientation == 0) {
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("Horizontal\n", null);
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("Horizontal\n", null);
            } else {
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("\nVertically\n", null);
                woyouService.printBitmap(bitmap, null);
                woyouService.printText("\nnVertically\n", null);
            }
            woyouService.lineWrap(3, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print Table
     */
    public void printTable(LinkedList<TableItem> list) {
        if (woyouService == null) {
            return;
        }
        try {
            for (TableItem tableItem : list) {
                woyouService.printColumnsString(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print Table Items
     */
    public void printTableItem(String[] text, int[] width, int[] align) {
        if (woyouService == null) {
            return;
        }
        try {
            woyouService.printColumnsString(text, width, align, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Three lines of empty
     */
    public void print3Line() {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendRawData(byte[] data) {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.sendRAWData(data, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendRawDataByBuffer(byte[] data, InnerResultCallbcak iCallback) {
        if (woyouService == null) {
            return;
        }

        try {
            woyouService.enterPrinterBuffer(true);
            woyouService.sendRAWData(data, iCallback);
            woyouService.exitPrinterBufferWithCallback(true, iCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cut (cut paper)
     */
    public void cutPaper() {
        try {
            woyouService.cutPaper(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
