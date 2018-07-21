package cz.jmpionyr.pstp.fusionreactor.loader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Camera {

    private static String TAG = "Camera";

    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    public static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static CameraManager getCameraManager(Context context) {
        return (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    public static String getCamera(CameraManager cameraManager) {

        if (cameraManager == null) {
            return null;
        }

        try {
            for (String cameraID : cameraManager.getCameraIdList()) {

                // Get camera characteristics.
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);

                // Is the camera facing front?
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                Log.d(TAG, String.format("Found camera with ID: %s", cameraID));
                return cameraID;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera failed to be found.", e);
        }

        Log.d(TAG, "No camera was found.");
        return null;
    }


    public static Size getCameraSize(CameraManager cameraManager, String cameraID, int width, int height) {
        CameraCharacteristics characteristics = getCharacteristics(cameraManager, cameraID);
        List<Size> sizes = getAvailableSizes(characteristics);
        return chooseBestSize(sizes, width, height);
    }

    public static CameraCharacteristics getCharacteristics(CameraManager cameraManager, String cameraID) {

        if (cameraManager == null || cameraID == null) {
            return null;
        }

        try {
            return cameraManager.getCameraCharacteristics(cameraID);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera characteristics failed to be found.", e);
        }

        return null;
    }

    public static List<Size> getAvailableSizes(CameraCharacteristics characteristics) {
        List<Size> sizes = new ArrayList<>();

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map != null) {

            for (Size size : map.getOutputSizes(SurfaceTexture.class)) {
                if (size.getHeight() <= MAX_PREVIEW_HEIGHT && size.getWidth() <= MAX_PREVIEW_WIDTH) {
                    sizes.add(size);
                }
            }
        }

        return sizes;
    }

    public static Size chooseBestSize(List<Size> sizes, int width, int height) {
        Log.d(TAG, String.format("Choosing optimal size for: %dx%d", width, height));

        // Get sizes and choose a default size.
        Size best = sizes.get(0);

        // Choose optimal size.
        for (Size size : sizes) {
            Log.d(TAG, String.format("Processing size %s", size.toString()));

            boolean is_small = size.getHeight() <= height && size.getWidth() <= width;
            boolean is_largest = size.getHeight() * size.getWidth() > best.getHeight() * best.getWidth();

            if (is_small && is_largest) {
                best = size;
            }
        }

        Log.d(TAG, String.format("Using size: %s", best.toString()));
        return best;
    }

    @SuppressLint("MissingPermission")
    public static void openCamera(CameraManager cameraManager, String cameraID, Handler handler, CameraDevice.StateCallback callback) {
        try {
            cameraManager.openCamera(cameraID, callback, handler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera couldn't be opened.", e);
        }
    }

    public static void createPreviewSession(CameraDevice device, Handler handler, Surface surface, CameraCaptureSession.StateCallback callback) {
        try {
            device.createCaptureSession(Collections.singletonList(surface), callback, handler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Session couldn't be created", e);
        }
    }

    public static void createPreviewRequest(CameraDevice device, Handler handler, Surface surface, CameraCaptureSession session) {
        try {
            CaptureRequest.Builder requestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            requestBuilder.addTarget(surface);

            CaptureRequest request =  requestBuilder.build();
            session.setRepeatingRequest(request, null, handler);

        } catch (CameraAccessException e) {
            Log.e(TAG, "Request couldn't be created.", e);
        }
    }

    public static void closeCameraHandler(Handler handler) {
        Log.d(TAG, "Closing the camera handler.");

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static void closePreviewSession(CameraCaptureSession session) {
        Log.d(TAG, "Closing the preview session.");

        if (session != null) {
            session.close();
        }
    }

    public static void closeCamera(CameraDevice device) {
        Log.d(TAG, "Closing the camera device.");

        if (device != null) {
            device.close();
        }
    }

}
