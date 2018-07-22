package cz.jmpionyr.pstp.fusionreactor.loader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import cz.jmpionyr.pstp.fusionreactor.R;
import cz.jmpionyr.pstp.fusionreactor.experiment.ExperimentActivity;
import cz.jmpionyr.pstp.fusionreactor.ui.TextView;

public class LoaderActivity extends Activity {

    public static final int LOAD_QR_CODES_REQUEST = 1;

    private static final String TAG = "LoaderActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 1;

    private String first_reactant;
    private String second_reactant;
    private TextView first_message;
    private TextView second_message;
    private MediaPlayer alertPlayer;

    private Handler main_handler;
    private boolean ignore_messages;

    private CameraPreview cameraPreview;
    private BarcodeDetector detector;
    private HandlerThread detector_thread;
    private Handler detector_handler;

    private final Runnable detector_watcher = new Runnable() {

        @Override
        public void run() {
            if (detector == null) {
                // Quit if there is no detector.
                return;
            }
            else if (!detector.isOperational()) {
                // Update the view.
                setFirstMessage("Detektor stahuje knihovny");

                // Continue to wait for the detector.
                main_handler.postDelayed(detector_watcher, 1000);
            }
            else {
                // Start the background thread.
                detector_thread = new HandlerThread("BarcodeDetection");
                detector_thread.start();

                // Start the background handler.
                detector_handler = new Handler(detector_thread.getLooper(), detector_callback);
                cameraPreview.setBitmapHandler(detector_handler);

                // Update the current view.
                updateMessages();
            }
        }
    };

    private final Handler.Callback detector_callback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // Empty the queue of messages.
            detector_handler.removeMessages(msg.what);

            // Try to detect the barcode on the received message.
            String barcode = detectBarcode((Bitmap) msg.obj);

            // Send reply to the target.
            if (barcode != null) {
                Message reply = Message.obtain();
                reply.obj = barcode;

                main_handler.sendMessage(reply);
            }

            return true;
        }
    };

    private final Handler.Callback main_callback = new Handler.Callback() {

        private final Runnable go = new Runnable() {
            @Override
            public void run() {
                ignore_messages = false;
            }
        };

        private final Runnable stop = new Runnable() {
            @Override
            public void run() {
                onReactantsAreSet();
            }
        };

        @Override
        public boolean handleMessage(Message msg) {

            if (ignore_messages) {
                return true;
            }

            String reactant = (String) msg.obj;

            // Set the current reactant.
            setReactant(reactant);

            // Update the view.
            updateMessages();

            // Play sound.
            alertPlayer.start();

            // Ignore messages for a while.
            ignore_messages = true;

            // Add a callback.
            if (areReactantsSet()) {
                main_handler.postDelayed(stop, 1000);
            }
            else {
                main_handler.postDelayed(go, 2000);
            }

            // Return true, because the message was processed.
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String title = "Priprava reaktantu";
        TextView.applyToActionBar(this, getActionBar(), title);

        setContentView(R.layout.activity_loader);

        cameraPreview = findViewById(R.id.camera_preview);
        first_message = findViewById(R.id.firstReactantView);
        second_message = findViewById(R.id.secondReactantView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkCameraPermissions()) {
            setFirstMessage("Kamera neni povolena");
            return;
        }

        // Create the media player.
        alertPlayer = MediaPlayer.create(this, R.raw.loader_alert);

        // Don't ignore main handler messages.
        ignore_messages = false;

        // Start the main handler.
        main_handler = new Handler(main_callback);

        // Create the detector.
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        // Update the view.
        detector_watcher.run();
    }

    private boolean checkCameraPermissions() {
        // Check the camera permissions.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            Log.d(TAG, "Camera permissions requested.");
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                cameraPreview.reloadCamera();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.e(TAG, "Permissions are not granted.");
            }
        }
    }

    private void setFirstMessage(String message) {
        first_message.setText(message);
        second_message.setVisibility(View.GONE);
    }

    private void setSecondMessage(String message) {
        second_message.setText(message);
        second_message.setVisibility(View.VISIBLE);
    }

    private void updateMessages() {
        if (first_reactant == null) {
            setFirstMessage("Nactete reaktant #1");
        }
        else{
            setFirstMessage(String.format("Reaktant #1: %s", first_reactant));

            if (second_reactant == null) {
                setSecondMessage("Nactete reaktant #2");
            }
            else {
                setSecondMessage(String.format("Reaktant #2: %s", second_reactant));
            }
        }
    }

    private String detectBarcode(Bitmap imageBitmap) {

        if (imageBitmap == null) {
            Log.d(TAG, "No bitmap to process.");
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

    private void setReactant(String reactant) {
        if (first_reactant == null) {
            Log.d(TAG, String.format("Reactant #1: %s", reactant));
            first_reactant = reactant;
        }
        else if (second_reactant == null) {
            Log.d(TAG, String.format("Reaktant #2: %s", reactant));
            second_reactant = reactant;
        }
    }

    private boolean areReactantsSet() {
        return first_reactant != null && second_reactant != null;
    }

    private void onReactantsAreSet() {
        Intent result = new Intent();
        result.putExtra(ExperimentActivity.FIRST_REACTANT, first_reactant);
        result.putExtra(ExperimentActivity.SECOND_REACTANT, second_reactant);

        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onPause() {

        if (cameraPreview != null) {
            cameraPreview.releaseCamera();
        }

        if (detector_thread != null) {
            detector_thread.quitSafely();
            try {
                detector_thread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed to join the detector thread.", e);
            }
            detector_thread = null;
        }

        if (detector_handler != null) {
            detector_handler.removeCallbacksAndMessages(null);
            detector_handler = null;
        }

        if (detector != null) {
            detector.release();
            detector = null;
        }

        if (alertPlayer != null) {
            alertPlayer.release();
            alertPlayer = null;
        }

        super.onPause();
    }
}
