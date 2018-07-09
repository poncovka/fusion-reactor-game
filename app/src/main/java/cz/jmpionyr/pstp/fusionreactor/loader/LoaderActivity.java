package cz.jmpionyr.pstp.fusionreactor.loader;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.Collections;

import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoaderActivity extends Activity {

    private final String TAG = "LoaderActivity";
    private final int CAMERA_PERMISSION_REQUEST = 1;

    private Surface surface;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        // Wait for surface.
        TextureView view = findViewById(R.id.texture_view);
        view.setSurfaceTextureListener(surfaceListener);
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
                session.setRepeatingRequest(request, sessionCaptureCallback, new Handler());
            } catch (CameraAccessException e) {
                Log.e(TAG, "The request failed to be set.", e);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "Session configuration has failed.");
        }
    };

    private final CameraCaptureSession.CaptureCallback sessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            // Preview request is ready.
            // Log.d(TAG, "The request is completed.");
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
            cameraSession.close();
            cameraSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        super.onPause();
    }
}
