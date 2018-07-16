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

import java.util.Collections;

/**
 * TODO: document your custom view class.
 */
public class CameraPreview extends TextureView {

    private static final String TAG = "CameraPreview";
    public static final int BITMAP_AVAILABLE = 1;

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

    public void setRatio(Size ratio) {

        if (ratio == null || ratio.getWidth() == 0 || ratio.getHeight() == 0) {
            ratio = null;
        }

        this.ratio = ratio;
    }

    public void reload() {
        cameraHandler = new Handler();

        CameraManager cameraManager = getCameraManager();
        String cameraID = getCamera(cameraManager);

        CameraCharacteristics characteristics = getCameraCharacteristics(cameraManager, cameraID);
        int orientation = getResources().getConfiguration().orientation;
        applyCameraCharacteristics(characteristics, orientation);

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

    public void applyCameraCharacteristics(CameraCharacteristics characteristics, int orientation) {

        StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (configurationMap == null) {
            return;
        }

        // TODO: Choose optimal size.

        Size[] sizes = configurationMap.getOutputSizes(SurfaceTexture.class);
        Size ratio = sizes[0];

        //SurfaceTexture texture = getSurfaceTexture();
        //texture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            ratio = new Size(ratio.getHeight(), ratio.getWidth());
        }

        setRatio(ratio);
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

        if (ratio == null) {
            setMeasuredDimension(width, height);
        }
        else if (width < height * ratio.getWidth() / ratio.getHeight()) {
            setMeasuredDimension(width, width * ratio.getHeight() / ratio.getWidth());
        }
        else {
            setMeasuredDimension(height * ratio.getWidth() / ratio.getHeight(), height);
        }
    }

    private final TextureView.SurfaceTextureListener surfaceListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            // Surface is ready.
            Log.d(TAG, "The surface is ready.");
            surface = new Surface(surfaceTexture);

            // Wait for camera.
            reload();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

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
