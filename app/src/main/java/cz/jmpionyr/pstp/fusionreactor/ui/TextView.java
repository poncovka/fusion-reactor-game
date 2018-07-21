package cz.jmpionyr.pstp.fusionreactor.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import cz.jmpionyr.pstp.fusionreactor.R;

public class TextView extends android.widget.TextView {

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
        setTextSize(20.0f);
    }


}
