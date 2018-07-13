package cz.jmpionyr.pstp.fusionreactor.reactor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cz.jmpionyr.pstp.fusionreactor.R;
import cz.jmpionyr.pstp.fusionreactor.experiment.ExperimentActivity;
import cz.jmpionyr.pstp.fusionreactor.loader.LoaderActivity;

public class ReactorActivity extends Activity {

    private static final String TAG = "ReactorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactor);
    }

    public void startExperiment(View view) {
        Intent intent = new Intent(this, LoaderActivity.class);
        startActivityForResult(intent, LoaderActivity.LOAD_QR_CODES_REQUEST);
    }

    private void runExperiment(String first, String second) {
        Intent intent = new Intent(this, ExperimentActivity.class);
        intent.putExtra(ExperimentActivity.FIRST_REACTANT, first);
        intent.putExtra(ExperimentActivity.SECOND_REACTANT, second);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoaderActivity.LOAD_QR_CODES_REQUEST) {
            if (resultCode == RESULT_OK) {
                String first = data.getStringExtra(ExperimentActivity.FIRST_REACTANT);
                String second = data.getStringExtra(ExperimentActivity.SECOND_REACTANT);
                runExperiment(first, second);
            }
        }
    }

    public void testSuccessfulExperiment(View view) {
        Log.d(TAG, "Starting suceessful experiment.");
        runExperiment("OHEN", "VZDUCH");
    }

    public void testUnsuccessfulExperiment(View view) {
        Log.d(TAG, "Starting unsuccessful experiment");
        runExperiment("OHEN", "ENERGIE");
    }

    public void quitReactor(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
