package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.jmpionyr.pstp.fusionreactor.R;

public class FailureFragment extends Fragment {

    public FailureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_failure, container, false);
    }
}
