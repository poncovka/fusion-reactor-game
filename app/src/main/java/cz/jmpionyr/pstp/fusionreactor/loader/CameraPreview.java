package cz.jmpionyr.pstp.fusionreactor.loader;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

public class CameraPreview extends TextureView {

    private static final String TAG = "CameraPreview";

    private Size ratio;
    private Handler handler;

    private Handler cameraHandler;
    private Surface cameraSurface;
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

    private void setRatio(int width, int height) {

        if (width == 0 || height == 0) {
            ratio = null;
            return;
        }

        ratio = new Size(width, height);
    }

    private Size getRatio() {

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

    public void reloadCamera() {
        // Check the camera permissions.
        if (!Camera.checkPermissions(getContext())){
            return;
        }

        // Try to find a camera.
        CameraManager cameraManager = Camera.getCameraManager(getContext());
        String cameraID = Camera.getCamera(cameraManager);

        // Set up the preview size.
        Size best = Camera.getCameraSize(cameraManager, cameraID, getMeasuredWidth(), getMeasuredHeight());
        resizePreview(best);

        // Create the camera handler.
        cameraHandler = new Handler();

        // Open the camera
        Camera.openCamera(cameraManager, cameraID, cameraHandler, cameraCallback);
    }

    private void resizePreview(Size size) {
        // Set the ratio of the view.
        setRatio(size.getWidth(), size.getHeight());

        // Set the default buffer size.
        SurfaceTexture texture = getSurfaceTexture();
        texture.setDefaultBufferSize(size.getWidth(), size.getHeight());

        // Change the layout.
        requestLayout();
    }


    public void setBitmapHandler(Handler handler) {
        this.handler = handler;
    }

    private void onBitmapChanged() {
        if (handler == null) {
            return;
        }

        Message msg = Message.obtain();
        msg.obj = getBitmap();
        handler.sendMessage(msg);
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
            reloadCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, String.format("Surface texture size changed %dx%d.", width, height));
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.d(TAG, "Destroying the surface.");
            cameraSurface = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            // The bitmap has changed.
            onBitmapChanged();
        }
    };

    private final CameraDevice.StateCallback cameraCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // Camera is ready.
            Log.d(TAG, "The camera is ready.");
            cameraDevice = camera;

            // Create a cameraSurface.
            cameraSurface = new Surface(getSurfaceTexture());

            // Wait for a preview session.
            Camera.createPreviewSession(cameraDevice, cameraHandler, cameraSurface, sessionCallback);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera is disconnected.");
            Camera.closeCamera(cameraDevice);
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, String.format("Camera failed with %d.", error));
            Camera.closeCamera(cameraDevice);
            cameraDevice = null;
        }
    };

    private final CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "The session is ready.");

            // Session is ready.
            cameraSession = session;

            // Create the preview request.
            Camera.createPreviewRequest(cameraDevice, cameraHandler, cameraSurface, cameraSession);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "Session configuration has failed.");
            Camera.closePreviewSession(cameraSession);
            cameraSession = null;
        }
    };

    public void releaseCamera() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        Camera.closeCamera(cameraDevice);
        cameraDevice = null;

        Camera.closeCameraHandler(cameraHandler);
        cameraHandler = null;

        Camera.closePreviewSession(cameraSession);
        cameraSession = null;
    }
}
