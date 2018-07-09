package cz.jmpionyr.pstp.fusionreactor.experiment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadyFragment extends Fragment {


    public ReadyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ready, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String reactant;
        TextView text_view;
        Bundle args = getArguments();

        // Set the first reactant.
        reactant = args.getString(ExperimentActivity.FIRST_REACTANT);
        text_view = getActivity().findViewById(R.id.first_reactant_text_view);
        text_view.setText(String.format("Reaktant #1: %s", reactant));

        // Set the second reactant.
        reactant = args.getString(ExperimentActivity.SECOND_REACTANT);
        text_view = getActivity().findViewById(R.id.second_reactant_text_view);
        text_view.setText(String.format("Reaktant #2: %s", reactant));
    }
}
