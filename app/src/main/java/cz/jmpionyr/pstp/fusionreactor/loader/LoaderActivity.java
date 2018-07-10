package cz.jmpionyr.pstp.fusionreactor.loader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Collections;

import cz.jmpionyr.pstp.fusionreactor.R;
import cz.jmpionyr.pstp.fusionreactor.experiment.ExperimentActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoaderActivity extends Activity {

    public static final int LOAD_QR_CODES_REQUEST = 1;

    private static final String TAG = "LoaderActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int DETECT_CODE_MESSAGE = 1;
    private static final int SET_REACTANT_MESSAGE = 2;

    private String first_reactant;
    private String second_reactant;

    private Handler main_handler;

    private CameraPreview cameraPreview;
    private Surface surface;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;

    private BarcodeDetector detector;
    private HandlerThread detector_thread;
    private Handler detector_handler;

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
                reply.what = SET_REACTANT_MESSAGE;
                reply.obj = barcode;

                main_handler.sendMessage(reply);
            }

            return true;
        }

        private String detectBarcode(Bitmap imageBitmap) {
            // Log.d(TAG, "Processing the bitmap.");

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
    };

    private final Handler.Callback main_callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String reactant = (String) msg.obj;
            setReactant(reactant);

            // TODO: Don't do this here.
            tryToFinish();

            // Return true, because the message was processed.
            return true;
        }
    };

    protected void setReactant(String reactant) {
        String message = "Nelze nastavit reaktant.";

        if (first_reactant == null) {
            first_reactant = reactant;
            message = String.format("Nastaven reaktant #1: %s", reactant);
        }
        else if (second_reactant == null) {
            second_reactant = reactant;
            message = String.format("Nastaven reaktant #2: %s", reactant);
        }

        Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void tryToFinish() {
        if (first_reactant != null && second_reactant != null) {
            Intent result = new Intent();
            result.putExtra(ExperimentActivity.FIRST_REACTANT, first_reactant);
            result.putExtra(ExperimentActivity.SECOND_REACTANT, second_reactant);

            setResult(RESULT_OK, result);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        // Start the main handler.
        main_handler = new Handler(main_callback);

        // Start the background thread.
        detector_thread = new HandlerThread("BarcodeDetection");
        detector_thread.start();

        // Start the background handler.
        detector_handler = new Handler(detector_thread.getLooper(), detector_callback);

        // Wait for surface.
        cameraPreview = findViewById(R.id.camera_preview);
        cameraPreview.setSurfaceTextureListener(surfaceListener);

        // Create the detector.
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

    }

    private final TextureView.SurfaceTextureListener surfaceListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            // Surface is ready.
            Log.d(TAG, "The surface is ready.");
            surface = new Surface(surfaceTexture);

            // Wait for camera.
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (detector_handler == null) {
                return;
            }

            Message msg = Message.obtain();
            msg.what = DETECT_CODE_MESSAGE;
            msg.obj = cameraPreview.getBitmap();
            detector_handler.sendMessage(msg);
        }
    };

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // Camera is ready.
            Log.d(TAG, "The camera is ready.");
            cameraDevice = camera;

            // Wait for a preview session.
            createPreviewSession(surface, cameraDevice);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera is disconnected.");
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, String.format("Camera failed with %d.", error));
            cameraDevice = null;
        }
    };

    private final CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "The session is ready.");

            // Session is ready.
            cameraSession = session;

            // Create the preview request.
            CaptureRequest request = createPreviewRequest(surface, cameraDevice);
            if (request == null) {
                Log.e(TAG, "The request failed to be created.");
                return;
            }

            // Wait for the preview request.
            try {
                session.setRepeatingRequest(request, null, new Handler());
            } catch (CameraAccessException e) {
                Log.e(TAG, "The request failed to be set.", e);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "Session configuration has failed.");
        }
    };

    private String getCamera(CameraManager cameraManager) {

        try {
            for (String cameraID : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                Log.d(TAG, String.format("Found camera: %s", cameraID));
                return cameraID;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera failed to be found.", e);
        }

        Log.d(TAG, "No camera was found.");
        return null;
    }

    private void openCamera() {

        // Check the camera permissions.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            Log.d(TAG, "Camera permissions requested.");
            return;
        }

        // Get the camera manager.
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        if (cameraManager == null) {
            Log.e(TAG, "Camera manager doesn't exist.");
            return;
        }

        // Get the camera ID.
        String cameraID = getCamera(cameraManager);
        if (cameraID == null) {
            Log.e(TAG, "No camera found.");
            return;
        }

        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            int orientation = getResources().getConfiguration().orientation;
            cameraPreview.applyCameraCharacteristics(characteristics, orientation);
            surface = new Surface(cameraPreview.getSurfaceTexture());

        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera characteristics failed.", e);
        }


        // Open the camera.
        try {
            cameraManager.openCamera(cameraID, cameraStateCallback, new Handler());
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera couldn't be opened.", e);
        }
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
                openCamera();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.e(TAG, "Permissions are not granted.");
            }
        }
    }

    private void createPreviewSession(Surface surface, CameraDevice cameraDevice) {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), sessionStateCallback, new Handler());
        } catch (CameraAccessException e) {
            Log.e(TAG, "Session couldn't be created", e);
        }
    }

    private CaptureRequest createPreviewRequest(Surface surface, CameraDevice cameraDevice) {
        try {
            CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            requestBuilder.addTarget(surface);
            return requestBuilder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, "Request couldn't be created.", e);
        }

        return null;
    }

    @Override
    protected void onPause() {

        if (cameraSession != null) {
            try {
                cameraSession.abortCaptures();
            } catch (CameraAccessException e) {
                Log.e(TAG, "Failed to abort captures.", e);
            }
            cameraSession.close();
            cameraSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (detector_thread != null) {
            detector_thread.quitSafely();
            try {
                detector_thread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed to join the detector thread.", e);
            }
            detector_thread = null;
            detector_handler = null;
        }

        if (detector != null) {
            detector.release();
            detector = null;
        }

        super.onPause();
    }
}
