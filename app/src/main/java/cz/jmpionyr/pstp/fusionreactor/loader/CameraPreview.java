package cz.jmpionyr.pstp.fusionreactor.loader;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Size;
import android.view.TextureView;
import android.view.View;

import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * TODO: document your custom view class.
 */
public class CameraPreview extends TextureView {

    private Size ratio;

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

    public void setRatio(Size ratio) {

        if (ratio == null || ratio.getWidth() == 0 || ratio.getHeight() == 0) {
            ratio = null;
        }

        this.ratio = ratio;
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

    private void init(AttributeSet attrs, int defStyle) {

    }

}
