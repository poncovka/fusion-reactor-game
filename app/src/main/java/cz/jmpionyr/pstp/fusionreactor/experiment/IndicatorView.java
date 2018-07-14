package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * TODO: document your custom view class.
 */
public class IndicatorView extends ImageView {

    public static List<Integer> getIndicators() {
        List<Integer> list = new ArrayList<>(8);

        list.add(R.id.indicator1);
        list.add(R.id.indicator2);
        list.add(R.id.indicator3);
        list.add(R.id.indicator4);
        list.add(R.id.indicator5);
        list.add(R.id.indicator6);
        list.add(R.id.indicator7);
        list.add(R.id.indicator8);

        return list;
    }


    public static List<Integer> getRandomIndicators(int count) {
        List<Integer> indicators = getIndicators();
        Collections.shuffle(indicators);
        return indicators.subList(0, count);
    }

    public static List<Integer> getImages() {
        List<Integer> list = new ArrayList<>(13);

        list.add(R.drawable.aliens);
        list.add(R.drawable.bomb);
        list.add(R.drawable.bug);
        list.add(R.drawable.cold);
        list.add(R.drawable.connection);
        list.add(R.drawable.electricity);
        list.add(R.drawable.fire);
        list.add(R.drawable.health);
        list.add(R.drawable.hot);
        list.add(R.drawable.measures);
        list.add(R.drawable.power);
        list.add(R.drawable.radiation);
        list.add(R.drawable.time);

        return list;
    }

    public static List<Integer> getRandomImages() {
        List<Integer> images = getImages();
        Collections.shuffle(images);
        return images;
    }

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
