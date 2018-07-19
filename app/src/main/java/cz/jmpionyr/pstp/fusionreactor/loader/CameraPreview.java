package cz.jmpionyr.pstp.fusionreactor.loader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class CameraPreview extends TextureView {

    private static final String TAG = "CameraPreview";
    public static final int BITMAP_AVAILABLE = 1;

    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private Size ratio;
    private Handler handler;
    private Surface surface;

    private Handler cameraHandler;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;

    public CameraPreview(Context context) {
        super(context);
        init(null, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.setSurfaceTextureListener(surfaceListener);
    }

    public void setBitmapHandler(Handler handler) {
        this.handler = handler;
    }

    public void setRatio(int width, int height) {

        if (ratio == null || width == 0 || height == 0) {
            ratio = null;
        }

        this.ratio = new Size(width, height);
    }

    public Size getRatio() {

        if (ratio == null) {
            return null;
        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return ratio;
        }
        else {
            return new Size(ratio.getHeight(), ratio.getWidth());
        }
    }

    public void reload() {
        cameraHandler = new Handler();

        CameraManager cameraManager = getCameraManager();
        String cameraID = getCamera(cameraManager);

        CameraCharacteristics characteristics = getCameraCharacteristics(cameraManager, cameraID);
        List<Size> sizes = getOutputSizes(characteristics);
        Size best = getBestSize(sizes);
        resizePreview(best);

        openCamera(cameraManager, cameraID);
    }

    private CameraManager getCameraManager() {
        return (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
    }

    private String getCamera(CameraManager cameraManager) {

        if (cameraManager == null) {
            return null;
        }

        try {
            for (String cameraID : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
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

    private CameraCharacteristics getCameraCharacteristics(CameraManager cameraManager, String cameraID) {

        if (cameraManager == null || cameraID == null) {
            return null;
        }

        try {
            return cameraManager.getCameraCharacteristics(cameraID);

        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera characteristics failed.", e);
        }

        return null;
    }

    private List<Size> getOutputSizes(CameraCharacteristics characteristics) {
        List<Size> sizes = new ArrayList<>();

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map != null) {

            for(Size size : map.getOutputSizes(SurfaceTexture.class)) {
                if (size.getHeight() <= MAX_PREVIEW_HEIGHT && size.getWidth() <= MAX_PREVIEW_WIDTH) {
                    sizes.add(size);
                }
            }
        }

        return sizes;
    }

    public Size getBestSize(List<Size> sizes) {

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

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

    public void resizePreview(Size size) {
        // Set the ratio of the view.
        setRatio(size.getWidth(), size.getHeight());

        // Set the default buffer size.
        SurfaceTexture texture = getSurfaceTexture();
        texture.setDefaultBufferSize(size.getWidth(), size.getHeight());

        // Change the layout.
        requestLayout();
    }

    private void openCamera(CameraManager cameraManager, String cameraID) {

        // Check the camera permissions.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Open the camera.
        try {
            cameraManager.openCamera(cameraID, cameraStateCallback, cameraHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera couldn't be opened.", e);
        }
    }

    private void createPreviewSession(Surface surface, CameraDevice cameraDevice) {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), sessionStateCallback, cameraHandler);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG, String.format("Measured dimensions are %dx%d.", width, height));

        Size ratio = getRatio();
        if (ratio != null) {

            Log.d(TAG, String.format("Using ratio %s.", ratio.toString()));

            // Keep the width, calculate the height.
            if (width > height * ratio.getWidth() / ratio.getHeight()) {
                Log.d(TAG, "Keeping the width.");
                height = width * ratio.getHeight() / ratio.getWidth();
            }
            // Keep the height, calculate the width.
           else {
                Log.d(TAG, "Keeping the height.");
                width = height * ratio.getWidth() / ratio.getHeight();
            }
        }

        // Set the new dimensions.
        Log.d(TAG, String.format("Setting measured dimensions to %dx%d.", width, height));
        setMeasuredDimension(width, height);
    }

    private final TextureView.SurfaceTextureListener surfaceListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            // Surface is ready.
            Log.d(TAG, String.format("The surface is ready with dimensions %dx%d.", width, height));

            // Wait for camera.
            reload();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, String.format("Surface texture size changed %dx%d.", width, height));
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.d(TAG, "Destroying the surface.");
            surface = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (handler == null) {
                return;
            }

            Message msg = Message.obtain();
            msg.what = BITMAP_AVAILABLE;
            msg.obj = getBitmap();
            handler.sendMessage(msg);
        }
    };

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // Camera is ready.
            Log.d(TAG, "The camera is ready.");
            cameraDevice = camera;

            // Wait for a preview session.
            surface = new Surface(getSurfaceTexture());
            createPreviewSession(surface, cameraDevice);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera is disconnected.");
            closeCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, String.format("Camera failed with %d.", error));
            closeCamera();
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
                session.setRepeatingRequest(request, null, cameraHandler);
            } catch (CameraAccessException e) {
                Log.e(TAG, "The request failed to be set.", e);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "Session configuration has failed.");
            closeSession();
        }
    };

    private void closeCamera() {
        Log.d(TAG, "Closing the camera.");

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeSession() {
        Log.d(TAG, "Closing the session.");

        if (cameraSession != null) {
            cameraSession.close();
            cameraSession = null;
        }
    }

    private void closeHandler() {
        Log.d(TAG, "Closing the handler.");

        if (cameraHandler != null) {
            cameraHandler.removeCallbacksAndMessages(null);
            cameraHandler = null;
        }

    }

    public void release() {
        handler = null;
        closeHandler();
        closeSession();
        closeCamera();
    }
}
