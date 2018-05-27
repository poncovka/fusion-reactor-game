package cz.jmpionyr.pstp.fusionreactor;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cz.jmpionyr.pstp.fusionreactor.experiment.LoadInputFragment;
import cz.jmpionyr.pstp.fusionreactor.experiment.RunFragment;
import cz.jmpionyr.pstp.fusionreactor.experiment.SummaryFragment;

public class ExperimentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            LoadInputFragment fragment = new LoadInputFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void loadQRCode(View view) {
        //Intent intent = new Intent(this, ExperimentActivity.class);
        //startActivity(intent);
        showSummary(view);
    }

    public void showSummary(View view) {
        SummaryFragment fragment = new SummaryFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void runExperiment(View view) {
        RunFragment fragment = new RunFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
