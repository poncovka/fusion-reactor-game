package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

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

        // Set up the view.
        setContentView(R.layout.activity_experiment);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            switchFragments(new RunningFragment());
        }
    }

    protected void switchFragments(Fragment fragment) {
        Bundle args = new Bundle();
        args.putInt(EXPERIMENT_ID, experiment_id);
        args.putString(FIRST_REACTANT, first_reactant);
        args.putString(SECOND_REACTANT, second_reactant);
        args.putString(PRODUCT, product);
        fragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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
