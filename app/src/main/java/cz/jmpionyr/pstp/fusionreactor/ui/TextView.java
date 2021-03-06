package cz.jmpionyr.pstp.fusionreactor.ui;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import cz.jmpionyr.pstp.fusionreactor.R;

public class TextView extends android.widget.TextView {

    public static void applyToActionBar(Context context, ActionBar bar, String title) {
        if (context == null || bar == null) {
            return;
        }

        TextView view = new TextView(context);
        view.setText(title);

        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.setCustomView(view);
    }

    public TextView(Context context) {
        super(context);
        init(null, 0);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.main_font);
        setTypeface(tf);
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.0f);
    }

    public void setImportant(boolean isImportant) {

        // Clear previous animations.
        this.clearAnimation();

        // Do nothing.
        if (!isImportant) {
            return;
        }

        // Start animation.
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(800);

        this.startAnimation(animation);
    }

}
