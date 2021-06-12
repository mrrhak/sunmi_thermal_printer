package com.mrrhak.sunmi_thermal_printer.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ESCUtil {
    public static final byte ESC = 0x1B;// Escape
    public static final byte FS = 0x1C;// Text delimiter
    public static final byte GS = 0x1D;// Group separator
    public static final byte DLE = 0x10;// data link escape
    public static final byte EOT = 0x04;// End of transmission
    public static final byte ENQ = 0x05;// Enquiry character
    public static final byte SP = 0x20;// Spaces
    public static final byte HT = 0x09;// Horizontal list
    public static final byte LF = 0x0A;// Print and wrap (horizontal orientation)
    public static final byte CR = 0x0D;// Home key
    public static final byte FF = 0x0C;// Carriage control (print and return to the standard mode (in page mode))
    public static final byte CAN = 0x18;// Canceled (cancel print data in page mode)

    // Initialize the printer
    public static byte[] init_printer() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 0x40;
        return result;
    }

    // Print density command
    public static byte[] setPrinterDarkness(int value) {
        byte[] result = new byte[9];
        result[0] = GS;
        result[1] = 40;
        result[2] = 69;
        result[3] = 4;
        result[4] = 0;
        result[5] = 5;
        result[6] = 5;
        result[7] = (byte) (value >> 8);
        result[8] = (byte) value;
        return result;
    }

    /**
     * Print a single QR code sunmi custom command
     *
     * @param code:       QR code data
     * @param moduleSize: Two-dimensional code block size (unit: point, value 1 to 16)
     * @param errorlevel: QR code error correction level (0 to 3)
     * 0 - Error correction level L (7%) 
     * 1 - Error correction level M (15%)
     * 2 - Error correction level Q (25%)
     * 3 - Error correction level H (30%)
     */
    public static byte[] getPrintQRCode(String code, int moduleSize, int errorlevel) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(setQRCodeSize(moduleSize));
            buffer.write(setQRCodeErrorLevel(errorlevel));
            buffer.write(getQCodeBytes(code));
            buffer.write(getBytesForPrintQRCode(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    /**
     * Two horizontal two-dimensional codes sunmi custom instructions
     *
     * @param code1:      QR code data
     * @param code2:      QR code data
     * @param moduleSize: Two-dimensional code block size (unit: point, value 1 to 16)
     * @param errorlevel: QR code error correction level (0 to 3)
     * 0 - Error correction level L (7%) 
     * 1 - Error correction level M (15%)
     * 2 - Error correction level Q (25%)
     * 3 - Error correction level H (30%)
     */
    public static byte[] getPrintDoubleQRCode(String code1, String code2, int moduleSize, int errorlevel) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(setQRCodeSize(moduleSize));
            buffer.write(setQRCodeErrorLevel(errorlevel));
            buffer.write(getQCodeBytes(code1));
            buffer.write(getBytesForPrintQRCode(false));
            buffer.write(getQCodeBytes(code2));

            // add horizontal interval
            buffer.write(new byte[] { 0x1B, 0x5C, 0x18, 0x00 });

            buffer.write(getBytesForPrintQRCode(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    /**
     * Printing QR code
     */
    public static byte[] getPrintQRCode2(String data, int size) {
        byte[] bytes1 = new byte[4];
        bytes1[0] = GS;
        bytes1[1] = 0x76;
        bytes1[2] = 0x30;
        bytes1[3] = 0x00;

        byte[] bytes2 = BytesUtil.getZXingQRCode(data, size);
        return BytesUtil.byteMerger(bytes1, bytes2);
    }

    /**
     * Print 1D Barcode
     */
    public static byte[] getPrintBarCode(String data, int symbology, int height, int width, int textPosition) {
        if (symbology < 0 || symbology > 10) {
            return new byte[] { LF };
        }

        if (width < 2 || width > 6) {
            width = 2;
        }

        if (textPosition < 0 || textPosition > 3) {
            textPosition = 0;
        }

        if (height < 1 || height > 255) {
            height = 162;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(new byte[] { 0x1D, 0x66, 0x01, 0x1D, 0x48, (byte) textPosition, 0x1D, 0x77, (byte) width, 0x1D,
                    0x68, (byte) height, 0x0A });

            byte[] barcode;
            if (symbology == 10) {
                barcode = BytesUtil.getBytesFromDecString(data);
            } else {
                barcode = data.getBytes("GB18030");
            }

            if (symbology > 7) {
                buffer.write(new byte[] { 0x1D, 0x6B, 0x49, (byte) (barcode.length + 2), 0x7B,
                        (byte) (0x41 + symbology - 8) });
            } else {
                buffer.write(new byte[] { 0x1D, 0x6B, (byte) (symbology + 0x41), (byte) barcode.length });
            }
            buffer.write(barcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    // Bitmap printing Set mode
    public static byte[] printBitmap(Bitmap bitmap, int mode) {
        byte[] bytes1 = new byte[4];
        bytes1[0] = GS;
        bytes1[1] = 0x76;
        bytes1[2] = 0x30;
        bytes1[3] = (byte) mode;

        byte[] bytes2 = BytesUtil.getBytesFromBitMap(bitmap);
        return BytesUtil.byteMerger(bytes1, bytes2);
    }

    // Bitmap printing
    public static byte[] printBitmap(Bitmap bitmap) {
        byte[] bytes1 = new byte[4];
        bytes1[0] = GS;
        bytes1[1] = 0x76;
        bytes1[2] = 0x30;
        bytes1[3] = 0x00;

        byte[] bytes2 = BytesUtil.getBytesFromBitMap(bitmap);
        return BytesUtil.byteMerger(bytes1, bytes2);
    }

    // Bitmap printing
    public static byte[] printBitmap(byte[] bytes) {
        byte[] bytes1 = new byte[4];
        bytes1[0] = GS;
        bytes1[1] = 0x76;
        bytes1[2] = 0x30;
        bytes1[3] = 0x00;

        return BytesUtil.byteMerger(bytes1, bytes);
    }

    /**
     * Select the bitmap command. Set the mode. You need to set 1B 33 00 and set the line spacing to 0.
     */
    public static byte[] selectBitmap(Bitmap bitmap, int mode) {
        return BytesUtil.byteMerger(
                BytesUtil.byteMerger(new byte[] { 0x1B, 0x33, 0x00 }, BytesUtil.getBytesFromBitMap(bitmap, mode)),
                new byte[] { 0x0A, 0x1B, 0x32 });
    }

    /**
     * Skip specified number of lines
     */
    public static byte[] nextLine(int lineNum) {
        byte[] result = new byte[lineNum];
        for (int i = 0; i < lineNum; i++) {
            result[i] = LF;
        }

        return result;
    }

    // ------------------------underline-----------------------------
    // Set underline 1 point
    public static byte[] underlineWithOneDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 1;
        return result;
    }

    // Set underline 2 points
    public static byte[] underlineWithTwoDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 2;
        return result;
    }

    // Suppress underline
    public static byte[] underlineOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 0;
        return result;
    }

    // ------------------------bold-----------------------------

    /**
     * Font bold
     */
    public static byte[] boldOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }

    /**
     * Unbold font
     */
    public static byte[] boldOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }

    // ------------------------character-----------------------------
    /**
     * Single byte mode on
     */
    public static byte[] singleByte() {
        byte[] result = new byte[2];
        result[0] = FS;
        result[1] = 0x2E;
        return result;
    }

    /**
     * Single byte mode off
     */
    public static byte[] singleByteOff() {
        byte[] result = new byte[2];
        result[0] = FS;
        result[1] = 0x26;
        return result;
    }

    /**
     * Set single-byte character set
     */
    public static byte[] setCodeSystemSingle(byte charset) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x74;
        result[2] = charset;
        return result;
    }

    /**
     * Set multi-byte character set
     */
    public static byte[] setCodeSystem(byte charset) {
        byte[] result = new byte[3];
        result[0] = FS;
        result[1] = 0x43;
        result[2] = charset;
        return result;
    }

    // ------------------------Align-----------------------------

    /**
     * Align left
     */
    public static byte[] alignLeft() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        return result;
    }

    /**
     * Align center
     */
    public static byte[] alignCenter() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        return result;
    }

    /**
     * Align right
     */
    public static byte[] alignRight() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        return result;
    }

    private static byte[] setQRCodeSize(int moduleSize) {
        // Two-dimensional code block size setting instruction
        byte[] dtmp = new byte[8];
        dtmp[0] = GS;
        dtmp[1] = 0x28;
        dtmp[2] = 0x6B;
        dtmp[3] = 0x03;
        dtmp[4] = 0x00;
        dtmp[5] = 0x31;
        dtmp[6] = 0x43;
        dtmp[7] = (byte) moduleSize;
        return dtmp;
    }

    private static byte[] setQRCodeErrorLevel(int errorlevel) {
        // QR code error correction level setting instruction
        byte[] dtmp = new byte[8];
        dtmp[0] = GS;
        dtmp[1] = 0x28;
        dtmp[2] = 0x6B;
        dtmp[3] = 0x03;
        dtmp[4] = 0x00;
        dtmp[5] = 0x31;
        dtmp[6] = 0x45;
        dtmp[7] = (byte) (48 + errorlevel);
        return dtmp;
    }

    private static byte[] getBytesForPrintQRCode(boolean single) {
        // Print the QR code of the stored data
        byte[] dtmp;
        if (single) { // Only one QRCode is printed on the same line, followed by a line feed
            dtmp = new byte[9];
            dtmp[8] = 0x0A;
        } else {
            dtmp = new byte[8];
        }
        dtmp[0] = 0x1D;
        dtmp[1] = 0x28;
        dtmp[2] = 0x6B;
        dtmp[3] = 0x03;
        dtmp[4] = 0x00;
        dtmp[5] = 0x31;
        dtmp[6] = 0x51;
        dtmp[7] = 0x30;
        return dtmp;
    }

    private static byte[] getQCodeBytes(String code) {
        // QR code deposit instruction
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            byte[] d = code.getBytes("GB18030");
            int len = d.length + 3;
            if (len > 7092)
                len = 7092;
            buffer.write((byte) 0x1D);
            buffer.write((byte) 0x28);
            buffer.write((byte) 0x6B);
            buffer.write((byte) len);
            buffer.write((byte) (len >> 8));
            buffer.write((byte) 0x31);
            buffer.write((byte) 0x50);
            buffer.write((byte) 0x30);
            for (int i = 0; i < d.length && i < len; i++) {
                buffer.write(d[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }
}
