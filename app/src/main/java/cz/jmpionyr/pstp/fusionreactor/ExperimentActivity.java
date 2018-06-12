package cz.jmpionyr.pstp.fusionreactor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import cz.jmpionyr.pstp.fusionreactor.experiment.LoadFragment;
import cz.jmpionyr.pstp.fusionreactor.experiment.ReadyFragment;
import cz.jmpionyr.pstp.fusionreactor.experiment.RunFragment;

public class ExperimentActivity extends Activity {

    private int number;
    private String reactant_1;
    private String reactant_2;

    private BarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

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
            LoadFragment fragment = new LoadFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void loadQRCode(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (imageBitmap == null) {
                Toast.makeText(this, "Nepodařilo se zpracovat data.", Toast.LENGTH_SHORT).show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if (barcodes.size() == 0) {
                Toast.makeText(this, "Nedetekován žádný kód.", Toast.LENGTH_SHORT).show();
                return;
            }

            Barcode barcode = barcodes.valueAt(0);

            if (reactant_1 == null) {
                reactant_1 = barcode.displayValue;
                Toast.makeText(this, String.format("Reaktant #1: %s",reactant_1), Toast.LENGTH_SHORT).show();
            }
            else if (reactant_2 == null) {
                reactant_2 = barcode.displayValue;
                Toast.makeText(this, String.format("Reaktant #2: %s", reactant_2), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Nelze nastavit reaktant.", Toast.LENGTH_SHORT).show();
            }
        }

        if (reactant_1 != null && reactant_2 != null) {
            ReadyFragment fragment = new ReadyFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void runExperiment(View view) {
        RunFragment fragment = new RunFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
