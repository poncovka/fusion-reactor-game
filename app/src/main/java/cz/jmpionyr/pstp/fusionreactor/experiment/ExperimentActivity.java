package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import cz.jmpionyr.pstp.fusionreactor.R;

public class ExperimentActivity extends Activity {

    public static final String EXPERIMENT_ID = "experimentId";
    public static final String FIRST_REACTANT = "firstReactant";
    public static final String SECOND_REACTANT = "secondReactant";
    public static final String PRODUCT = "product";

    private int experiment_id;
    private String first_reactant;
    private String second_reactant;
    private String product;

    private static final int EXPERIMENT_STATE_CHANGED = 1;

    private Handler handler;

    private final Handler.Callback handler_callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case EXPERIMENT_STATE_CHANGED:

                    break;
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the instance state.
        if (savedInstanceState != null) {
            loadInstanceState(savedInstanceState);
        }
        else {
            Random random = new Random();
            experiment_id = random.nextInt(90000) + 10000;

            Intent intent = getIntent();
            first_reactant = intent.getStringExtra(FIRST_REACTANT);
            second_reactant = intent.getStringExtra(SECOND_REACTANT);
        }

        // Set up the handler.
        handler = new Handler(handler_callback);

        // Set up the view.
        setContentView(R.layout.activity_experiment);

        // Prepare for the experiment.
        onExperimentReady();
    }


    private void onExperimentReady() {
        Button button = findViewById(R.id.startButton);
        button.setText("Spustit");
        button.setClickable(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExperimentStarted();
            }
        });
    }

    private void onExperimentStarted() {
        Button button = findViewById(R.id.startButton);
        button.setText("Spuštěno");
        button.setClickable(false);
        planExperiment();
    }

    private void onExperimentFinished() {
        Button button = findViewById(R.id.startButton);
        button.setText("Ukončit");
        button.setClickable(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExperimentQuit();
            }
        });
    }

    private void onExperimentQuit() {
        Button button = findViewById(R.id.startButton);
        button.setText("Ukončeno");
        button.setClickable(false);

        // Quit the activity.
        finish();
    }

    private void planExperiment() {

        // Plan the experiment process.

        // TODO: Start the background music.

        // Choose 3 or 4 indicators.
        // Plan their indications.
        long delay = 1000; // wait a little after start
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IndicatorView indicator = findViewById(R.id.indicator4);
                indicator.indicateError();
            }
        }, delay);

        delay += 3000; // wait for the first indicator to stop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IndicatorView indicator = findViewById(R.id.indicator1);
                indicator.indicateError();
            }
        }, delay);

        delay += 3000; // wait for the second indicator to stop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IndicatorView indicator = findViewById(R.id.indicator8);
                indicator.indicateError();
            }
        }, delay);

        delay += 3000; // wait for the third indicator to stop
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onExperimentFinished();
            }
        }, delay);


        // Plan one progress report.

        // Stop the background music.

        // Set the result.

        // Tell the result.

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
}
