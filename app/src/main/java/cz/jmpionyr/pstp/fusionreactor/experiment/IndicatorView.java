package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
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
        setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
    }

    public void indicateError() {
        setColorFilter(null);

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setRepeatCount(2);
        animation.setDuration(800);
        animation.setInterpolator(new LinearInterpolator());

        this.startAnimation(animation);
    }

}
