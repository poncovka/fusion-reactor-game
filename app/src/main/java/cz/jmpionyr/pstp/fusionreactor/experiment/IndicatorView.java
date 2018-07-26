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

    public void animateError() {
        setColorFilter(null);

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setRepeatCount(2);
        animation.setDuration(800);
        animation.setInterpolator(new LinearInterpolator());
        this.startAnimation(animation);
    }

    public void showError() {
        setColorFilter(null);
        setAlpha(1.0f);
    }

    private static List<Integer> getImages() {
        List<Integer> list = new ArrayList<>(13);

        list.add(R.drawable.alarm);
        list.add(R.drawable.alien);
        list.add(R.drawable.analysis);
        list.add(R.drawable.antenna);
        list.add(R.drawable.antivirus);
        list.add(R.drawable.ash);
        list.add(R.drawable.atom);
        list.add(R.drawable.attention);
        list.add(R.drawable.bacteria);
        list.add(R.drawable.banned);
        list.add(R.drawable.battery);
        list.add(R.drawable.biohazard);
        list.add(R.drawable.bomb);
        list.add(R.drawable.bottle);
        list.add(R.drawable.box);
        list.add(R.drawable.bug);
        list.add(R.drawable.burn);
        list.add(R.drawable.calculator);
        list.add(R.drawable.cardiogram);
        list.add(R.drawable.chart);
        list.add(R.drawable.chemicals);
        list.add(R.drawable.chip);
        list.add(R.drawable.chronometer);
        list.add(R.drawable.cloud);
        list.add(R.drawable.co2);
        list.add(R.drawable.cold);
        list.add(R.drawable.computer);
        list.add(R.drawable.cone);
        list.add(R.drawable.connection);
        list.add(R.drawable.database);
        list.add(R.drawable.devices);
        list.add(R.drawable.disconnected);
        list.add(R.drawable.dna);
        list.add(R.drawable.electrocardiogram);
        list.add(R.drawable.electrocution);
        list.add(R.drawable.electron);
        list.add(R.drawable.eruption);
        list.add(R.drawable.experiment);
        list.add(R.drawable.extinguisher);
        list.add(R.drawable.file_virus);
        list.add(R.drawable.fire);
        list.add(R.drawable.flame);
        list.add(R.drawable.flask);
        list.add(R.drawable.fuell);
        list.add(R.drawable.grows);
        list.add(R.drawable.health);
        list.add(R.drawable.heart);
        list.add(R.drawable.increase);
        list.add(R.drawable.infection);
        list.add(R.drawable.laboratory);
        list.add(R.drawable.light);
        list.add(R.drawable.lightning);
        list.add(R.drawable.lungs);
        list.add(R.drawable.manometer);
        list.add(R.drawable.mask);
        list.add(R.drawable.microscope);
        list.add(R.drawable.monster);
        list.add(R.drawable.panel);
        list.add(R.drawable.poison);
        list.add(R.drawable.power);
        list.add(R.drawable.radar);
        list.add(R.drawable.radioactive);
        list.add(R.drawable.robot);
        list.add(R.drawable.rules);
        list.add(R.drawable.satellite);
        list.add(R.drawable.screen);
        list.add(R.drawable.scull);
        list.add(R.drawable.sensor);
        list.add(R.drawable.speedometer);
        list.add(R.drawable.suit);
        list.add(R.drawable.timer);
        list.add(R.drawable.toolbox);
        list.add(R.drawable.touch);
        list.add(R.drawable.toxic);
        list.add(R.drawable.tube);
        list.add(R.drawable.tubes);
        list.add(R.drawable.virus);
        list.add(R.drawable.volcano);
        list.add(R.drawable.wheat);
        list.add(R.drawable.window);

        return list;
    }
}
