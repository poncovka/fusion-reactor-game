package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * TODO: document your custom view class.
 */
public class IndicatorView extends ImageView {

    public IndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setImageResource(R.mipmap.ic_launcher_round);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
