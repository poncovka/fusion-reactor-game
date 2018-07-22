package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.jmpionyr.pstp.fusionreactor.R;
import cz.jmpionyr.pstp.fusionreactor.reactant.Reaction;
import cz.jmpionyr.pstp.fusionreactor.ui.TextView;

public class ExperimentActivity extends Activity {

    public static final String EXPERIMENT_ID = "experimentId";
    public static final String EXPERIMENT_STATE = "experimentState";
    public static final String FIRST_REACTANT = "firstReactant";
    public static final String SECOND_REACTANT = "secondReactant";
    public static final String PRODUCT = "product";

    private static final int EXPERIMENT_READY = 0;
    private static final int EXPERIMENT_STARTED = 1;
    private static final int EXPERIMENT_FINISHED = 2;
    private static final int EXPERIMENT_QUIT = 3;

    private int experiment_id;
    private int experiment_state;
    private String first_reactant;
    private String second_reactant;
    private String product;

    private Handler handler;
    private MediaPlayer backgroundPlayer;
    private MediaPlayer progressPlayer;
    private MediaPlayer resultPlayer;

    private Button button;
    private TextView reaction;
    private LinearLayout reactionLayout;
    private List<IndicatorView> errors;

    private boolean isExperimentSuccessful() {
        return product != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the instance state.
        if (savedInstanceState != null) {
            loadInstanceState(savedInstanceState);
        } else {
            // Generate the experiment id.
            Random random = new Random();
            experiment_id = random.nextInt(9000) + 1000;

            // Set the state of the experiment.
            experiment_state = EXPERIMENT_READY;

            // Get the reactants.
            Intent intent = getIntent();
            first_reactant = intent.getStringExtra(FIRST_REACTANT);
            second_reactant = intent.getStringExtra(SECOND_REACTANT);

            // Get the product.
            product = Reaction.getProduct(first_reactant, second_reactant);
        }

        String title = String.format("Experiment #%s", Integer.toString(experiment_id));
        TextView.applyToActionBar(this, getActionBar(), title);

        // Set up the view.
        setContentView(R.layout.activity_experiment);
        button = findViewById(R.id.startButton);
        reaction = findViewById(R.id.reactionView);
        reactionLayout = findViewById(R.id.reactionLayout);

        // Set up the indicators.
        initializeIndicators();
        errors = getErrorIndicators();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (experiment_state == EXPERIMENT_READY) {
            onExperimentReady();
        }
        else {
            onExperimentFinished();
        }
    }

    private void onExperimentReady() {
        // Set the state.
        experiment_state = EXPERIMENT_READY;

        // Set the reaction.
        reaction.setText(String.format("%s + %s = ?", first_reactant, second_reactant));

        // Set the button.
        button.setText("Spustit");
        button.setClickable(true);
        button.setBackgroundResource(R.drawable.button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExperimentStarted();
            }
        });
    }

    private void onExperimentStarted() {
        // Set the state.
        experiment_state = EXPERIMENT_STARTED;

        // Set the button.
        button.setText("Spusteno");
        button.setClickable(false);
        button.setBackgroundResource(R.drawable.button_start_clicked);

        // Plan the experiment.
        planExperiment();
    }

    private void onExperimentFinished() {
        // Set the state.
        experiment_state = EXPERIMENT_FINISHED;

        // Show indicatons.
        showIndications();

        // Set the reaction.
        reaction.setText(String.format("%s + %s = %s", first_reactant, second_reactant, getProduct()));
        reaction.setImportant(true);

        reactionLayout.setBackgroundResource(getBackgroundColor());

        // Set the button.
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
        // Set the state.
        experiment_state = EXPERIMENT_QUIT;

        // Set the button.
        button.setText("Ukonceno");
        button.setClickable(false);
        button.setBackgroundResource(R.drawable.button_start_clicked);

        // Quit the activity.
        finish();
    }

    private void planExperiment() {
        // Set the handler.
        handler = new Handler();

        // Prepare the sounds.
        backgroundPlayer = MediaPlayer.create(this, R.raw.reactor_background);
        progressPlayer = MediaPlayer.create(this, ExperimentSound.getRandomProgressMessage());
        resultPlayer = MediaPlayer.create(this, getResultMessage());

        // Start the background music.
        backgroundPlayer.start();

        // Plan the indicators.
        long delay = 1000; // wait a little after start

        for (IndicatorView indicator : errors) {
            planIndication(indicator, delay);
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

    private String getProduct() {
        if (isExperimentSuccessful()) {
            return product;
        }
        else {
            return "CHYBA";
        }
    }

    private int getErrorIndicatorsCount() {
        if (isExperimentSuccessful()) {
            return 3;
        }
        else {
            return 4;
        }
    }

    private int getBackgroundColor() {
        if (isExperimentSuccessful()) {
            return R.color.success_overlay;
        }
        else {
            return R.color.failure_overlay;
        }
    }

    private void initializeIndicators() {
        List<Integer> indicators = IndicatorView.getIndicators();
        List<Integer> images = IndicatorView.getRandomImages();

        for (int i = 0; i < indicators.size(); i++) {
            IndicatorView indicator = findViewById(indicators.get(i));
            indicator.setImageResource(images.get(i));
        }
    }

    private List<IndicatorView> getErrorIndicators() {
        int count = getErrorIndicatorsCount();
        List<IndicatorView> indicators = new ArrayList<>(count);

        for (int id : IndicatorView.getRandomIndicators(count)) {
            IndicatorView view = findViewById(id);
            indicators.add(view);
        }

        return indicators;
    }

    private void planIndication(final IndicatorView indicator, long delay) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                indicator.animateError();
            }
        }, delay);

    }

    private void showIndications() {
        if (errors == null) {
            return;
        }

        for (IndicatorView indicator : errors) {
            indicator.showError();
        }
    }

    private void loadInstanceState(Bundle savedInstanceState) {
        experiment_id = savedInstanceState.getInt(EXPERIMENT_ID);
        experiment_state = savedInstanceState.getInt(EXPERIMENT_STATE);
        first_reactant = savedInstanceState.getString(FIRST_REACTANT);
        second_reactant = savedInstanceState.getString(SECOND_REACTANT);
        product = savedInstanceState.getString(PRODUCT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXPERIMENT_ID, experiment_id);
        outState.putInt(EXPERIMENT_STATE, experiment_state);
        outState.putString(FIRST_REACTANT, first_reactant);
        outState.putString(SECOND_REACTANT, second_reactant);
        outState.putString(PRODUCT, product);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();


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
    }
}
