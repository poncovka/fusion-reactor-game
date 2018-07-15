package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import cz.jmpionyr.pstp.fusionreactor.R;
import cz.jmpionyr.pstp.fusionreactor.reactant.Reaction;

public class ExperimentActivity extends Activity {

    public static final String EXPERIMENT_ID = "experimentId";
    public static final String FIRST_REACTANT = "firstReactant";
    public static final String SECOND_REACTANT = "secondReactant";
    public static final String PRODUCT = "product";

    private int experiment_id;
    private String first_reactant;
    private String second_reactant;
    private String product;

    private Handler handler;
    private MediaPlayer backgroundPlayer;
    private MediaPlayer progressPlayer;
    private MediaPlayer resultPlayer;

    private boolean isExperimentSuccessful() {
        return product != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the instance state.
        if (savedInstanceState != null) {
            loadInstanceState(savedInstanceState);
        }
        else {
            // Generate the experiment id.
            Random random = new Random();
            experiment_id = random.nextInt(90000) + 10000;

            // Get the reactants.
            Intent intent = getIntent();
            first_reactant = intent.getStringExtra(FIRST_REACTANT);
            second_reactant = intent.getStringExtra(SECOND_REACTANT);

            // Get the product.
            product = Reaction.getProduct(first_reactant, second_reactant);
        }

        // Set up the handler.
        handler = new Handler();

        // Set up the view.
        setContentView(R.layout.activity_experiment);

        // Prepare for the experiment.
        onExperimentReady();
    }


    private void onExperimentReady() {
        TextView reaction = findViewById(R.id.reactionView);
        reaction.setText(String.format("%s + %s = ?", first_reactant, second_reactant));

        Button button = findViewById(R.id.startButton);
        button.setText("Spustit");
        button.setClickable(true);
        button.setBackgroundResource(R.drawable.button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExperimentStarted();
            }
        });

        List<Integer> indicators = IndicatorView.getIndicators();
        List<Integer> images = IndicatorView.getRandomImages();

        for (int i = 0; i < indicators.size(); i++) {
            IndicatorView indicator = findViewById(indicators.get(i));
            indicator.setImageResource(images.get(i));
        }
    }

    private void onExperimentStarted() {
        Button button = findViewById(R.id.startButton);
        button.setText("Spusteno");
        button.setClickable(false);
        button.setBackgroundResource(R.drawable.button_start_clicked);
        planExperiment();
    }

    private void onExperimentFinished() {
        TextView reaction = findViewById(R.id.reactionView);
        reaction.setText(String.format("%s + %s = %s", first_reactant, second_reactant, getProduct()));

        Button button = findViewById(R.id.startButton);
        button.setText("Ukoncit");
        button.setClickable(true);
        button.setBackgroundResource(R.drawable.button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExperimentQuit();
            }
        });
    }

    private void onExperimentQuit() {
        Button button = findViewById(R.id.startButton);
        button.setText("Ukonceno");
        button.setClickable(false);
        button.setBackgroundResource(R.drawable.button_start_clicked);

        // Quit the activity.
        finish();
    }

    private void planExperiment() {

        // Prepare the sounds.
        backgroundPlayer = MediaPlayer.create(this, R.raw.reactor_background);
        progressPlayer = MediaPlayer.create(this, ExperimentSound.getRandomProgressMessage());
        resultPlayer = MediaPlayer.create(this, getResultMessage());


        // Start the background music.
        backgroundPlayer.start();

        // TODO: Start the result generation.

        // Plan the indicators.
        long delay = 1000; // wait a little after start
        int indicators_count = getIndicatorsCount();

        for (int indicator : IndicatorView.getRandomIndicators(indicators_count)) {
            planErrorIndication(indicator, delay);
            delay += 4000; // wait for the indicator to stop
        }

        // Plan the progress message.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressPlayer.start();
            }
        }, delay/3);

        // Plan the experiment finish.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                backgroundPlayer.stop();
                resultPlayer.start();

                // TODO: set the result

                onExperimentFinished();
            }
        }, delay);
    }


    private int getResultMessage() {
        if (isExperimentSuccessful()) {
            return ExperimentSound.getRandomSuccessfulMessage();
        }
        else {
            return ExperimentSound.getRandomErrorMessage();
        }
    }

    private int getIndicatorsCount() {
        if (isExperimentSuccessful()) {
            return 3;
        }
        else {
            return 4;
        }
    }

    private String getProduct() {
        if (isExperimentSuccessful()) {
            return product;
        }
        else {
            return "CHYBA";
        }
    }

    private void planErrorIndication(final int indicator, long delay) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IndicatorView indicatorView = findViewById(indicator);
                indicatorView.indicateError();
            }
        }, delay);

    }

    protected void loadInstanceState(Bundle savedInstanceState) {
        experiment_id = savedInstanceState.getInt(EXPERIMENT_ID);
        first_reactant = savedInstanceState.getString(FIRST_REACTANT);
        second_reactant = savedInstanceState.getString(SECOND_REACTANT);
        product = savedInstanceState.getString(PRODUCT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXPERIMENT_ID, experiment_id);
        outState.putString(FIRST_REACTANT, first_reactant);
        outState.putString(SECOND_REACTANT, second_reactant);
        outState.putString(PRODUCT, product);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        if (backgroundPlayer != null) {
            backgroundPlayer.release();
            backgroundPlayer = null;
        }

        if (progressPlayer != null) {
            progressPlayer.release();
            progressPlayer = null;
        }

        if (resultPlayer != null) {
            resultPlayer.release();
            resultPlayer = null;
        }

        super.onPause();
    }
}
