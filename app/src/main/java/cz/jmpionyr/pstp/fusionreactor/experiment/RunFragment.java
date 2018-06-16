package cz.jmpionyr.pstp.fusionreactor.experiment;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import cz.jmpionyr.pstp.fusionreactor.ExperimentActivity;
import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunFragment extends Fragment {

    public static final String PERCENTS = "experimentPercents";
    private int percents;

    Handler experiment_handler = new Handler();
    private final Runnable experiment_runnable = new Runnable() {

        public void run() {

            percents += 1;

            ExperimentActivity activity = (ExperimentActivity) getActivity();
            TextView percents_text_view = activity.findViewById(R.id.percents_text_view);
            percents_text_view.setText(String.format(Locale.getDefault(), "%d%%", percents));

            if (percents < 100) {
                experiment_handler.postDelayed(experiment_runnable, 1000);
            }
            else {
                activity.onExperimentFinished();
            }
        }
    };

    public RunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            percents = savedInstanceState.getInt(PERCENTS);
        }
        else {
            percents = 0;
        }

        TextView percents_text_view = getActivity().findViewById(R.id.percents_text_view);
        percents_text_view.setText(String.format(Locale.getDefault(), "%d%%", percents));

        experiment_handler.postDelayed(experiment_runnable, 1000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PERCENTS, percents);
    }

    @Override
    public void onDetach() {
        experiment_handler.removeCallbacksAndMessages(null);
        super.onDetach();
    }
}
