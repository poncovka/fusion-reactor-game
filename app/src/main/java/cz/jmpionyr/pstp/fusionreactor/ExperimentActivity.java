package cz.jmpionyr.pstp.fusionreactor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Locale;
import java.util.Random;

import cz.jmpionyr.pstp.fusionreactor.experiment.LoadFragment;
import cz.jmpionyr.pstp.fusionreactor.experiment.ReadyFragment;
import cz.jmpionyr.pstp.fusionreactor.experiment.RunFragment;

public class ExperimentActivity extends Activity {

    public static final String EXPERIMENT_ID = "experimentId";
    public static final String FIRST_REACTANT = "firstReactant";
    public static final String SECOND_REACTANT = "secondReactant";
    public static final String PRODUCT = "product";

    private int experiment_id;
    private String first_reactant;
    private String second_reactant;
    private BarcodeDetector detector;

    private boolean testing = true;

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
        }

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        // Set up the view.
        setContentView(R.layout.activity_experiment);
        TextView textView = findViewById(R.id.experiment_id);
        textView.setText(String.format(Locale.getDefault(), "Experiment: #%d", experiment_id));

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Skip loading QR codes in the testing mode.
            if (testing) {
                setReactant("VODA");
                setReactant("ZEME");
                switchFragments(new RunFragment());
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            switchFragments(new LoadFragment());
        }
    }

    public void onLoadQRCode(View view) {
        loadQRCode();
    }

    protected void loadQRCode() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }

            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap == null) {
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if (barcodes.size() == 0) {
                Toast.makeText(this, "Reaktant nebyl detekován.", Toast.LENGTH_SHORT).show();
                return;
            }

            Barcode barcode = barcodes.valueAt(0);
            setReactant(barcode.displayValue);
        }

        if (first_reactant != null && second_reactant != null) {
            switchFragments(new ReadyFragment());
        }
    }

    protected void setReactant(String reactant) {
        String message = "Nelze nastavit reaktant.";

        if (first_reactant == null) {
            first_reactant = reactant;
            message = String.format("Nastaven reaktant #1: %s", reactant);
        }
        else if (second_reactant == null) {
            second_reactant = reactant;
            message = String.format("Nastaven reaktant #2: %s", reactant);
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onRunExperiment(View view) {
        switchFragments(new RunFragment());
    }

    protected void switchFragments(Fragment fragment) {
        Bundle args = new Bundle();
        args.putInt(EXPERIMENT_ID, experiment_id);
        args.putString(FIRST_REACTANT, first_reactant);
        args.putString(SECOND_REACTANT, second_reactant);

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXPERIMENT_ID, experiment_id);
        outState.putString(FIRST_REACTANT, first_reactant);
        outState.putString(SECOND_REACTANT, second_reactant);
        super.onSaveInstanceState(outState);
    }

}
