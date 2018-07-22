package cz.jmpionyr.pstp.fusionreactor.reactor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cz.jmpionyr.pstp.fusionreactor.R;
import cz.jmpionyr.pstp.fusionreactor.experiment.ExperimentActivity;
import cz.jmpionyr.pstp.fusionreactor.loader.LoaderActivity;
import cz.jmpionyr.pstp.fusionreactor.ui.TextView;

public class ReactorActivity extends Activity {

    private static final String TAG = "ReactorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String title = "Fuzni reaktor";
        TextView.applyToActionBar(this, getActionBar(), title);

        setContentView(R.layout.activity_reactor);
    }

    public void runLoader(View view) {
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
        runExperiment("ELEKTRICKY_DRAT", "SVETLO");
    }

    public void testUnsuccessfulExperiment(View view) {
        runExperiment("ROSTLINA", "KOV");
    }

    public void quitReactor(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
