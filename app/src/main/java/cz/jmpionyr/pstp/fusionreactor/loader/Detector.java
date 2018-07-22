package cz.jmpionyr.pstp.fusionreactor.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class Detector {

    public static BarcodeDetector getBarcodeDetector(Context context) {
        return new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
    }

    public static String detectBarcode(BarcodeDetector detector, Bitmap imageBitmap) {
        if (imageBitmap == null) {
            return null;
        }

        Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        if (barcodes.size() == 0) {
            return null;
        }

        Barcode barcode = barcodes.valueAt(0);
        return barcode.displayValue;
    }

    public static void closeBarcodeDetector(BarcodeDetector detector) {
        if (detector != null) {
            detector.release();
        }
    }
}
