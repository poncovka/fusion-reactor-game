package cz.jmpionyr.pstp.fusionreactor.experiment;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import cz.jmpionyr.pstp.fusionreactor.ExperimentActivity;
import cz.jmpionyr.pstp.fusionreactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunFragment extends Fragment {

    public static final String PERCENTS = "experimentPercents";
    private int percents;
    private boolean failure;

    private MediaPlayer backgroundPlayer;

    Handler experiment_handler = new Handler();
    private final Runnable experiment_runnable = new Runnable() {

        public void run() {

            percents += 1;

            final ExperimentActivity activity = (ExperimentActivity) getActivity();

            if (activity == null) {
                return;
            }

            if (percents > 100) {
                activity.onExperimentFinished();
                return;
            }

            TextView percents_text_view = activity.findViewById(R.id.percents_text_view);
            percents_text_view.setText(String.format(Locale.getDefault(), "%d%%", percents));
            ReactorView reactor_view = activity.findViewById(R.id.reactor_view);

            if (percents == 100) {
                backgroundPlayer.stop();

                MediaPlayer mediaPlayer = MediaPlayer.create(activity , R.raw.final_alert);
                mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        super.onCompletion(mp);
                        ReactorView reactor_view = getActivity().findViewById(R.id.reactor_view);
                        reactor_view.stopReactorAnimation();

                        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity() , R.raw.experiment_successful);
                        mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                super.onCompletion(mp);
                                experiment_handler.post(experiment_runnable);
                            }
                        });
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.start();
                return;
            }

            if (percents == 1) {
                reactor_view.startReactorAnimation();

                backgroundPlayer = MediaPlayer.create(getActivity() , R.raw.reactor);
                backgroundPlayer.start();

                MediaPlayer mediaPlayer = MediaPlayer.create(activity , R.raw.experiment_started);
                mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser());
                mediaPlayer.start();
            }
            else if (percents == 26) {
                MediaPlayer mediaPlayer = MediaPlayer.create(activity , R.raw.compatibility_testing);
                mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser());
                mediaPlayer.start();
            }
            else if (percents == 48 && failure) {
                MediaPlayer mediaPlayer = MediaPlayer.create(activity , R.raw.hard_alert);
                mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        super.onCompletion(mp);
                        backgroundPlayer.stop();

                        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity() , R.raw.compatibility_error);
                        mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                super.onCompletion(mp);
                                ((ExperimentActivity) getActivity()).onExperimentFinished();
                            }
                        });
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.start();
                return;
            }
            else if (percents == 48) {
                MediaPlayer mediaPlayer = MediaPlayer.create(activity , R.raw.reactor_works_89);
                mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser());
                mediaPlayer.start();
            }
            else if (percents == 71) {
                MediaPlayer mediaPlayer = MediaPlayer.create(activity , R.raw.radiation_leak_warning);
                mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser());
                mediaPlayer.start();
            }

            Random random = new Random();
            int delay = random.nextInt(300);

            experiment_handler.postDelayed(experiment_runnable, delay);
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

        Bundle args = getArguments();
        String product = args.getString(ExperimentActivity.PRODUCT);
        failure = product == null;

        if (savedInstanceState != null) {
            percents = savedInstanceState.getInt(PERCENTS);
        }
        else {
            percents = 0;
        }

        TextView percents_text_view = getActivity().findViewById(R.id.percents_text_view);
        percents_text_view.setText(String.format(Locale.getDefault(), "%d%%", percents));

        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity() , R.raw.experiment_starts);
        mediaPlayer.setOnCompletionListener(new MediaPlayerReleaser() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                super.onCompletion(mp);
                experiment_handler.post(experiment_runnable);
            }
        });
        mediaPlayer.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PERCENTS, percents);
    }

    @Override
    public void onDetach() {
        experiment_handler.removeCallbacksAndMessages(null);

        if (backgroundPlayer != null) {
            backgroundPlayer.release();
            backgroundPlayer = null;
        }

        super.onDetach();
    }
}

class MediaPlayerReleaser implements MediaPlayer.OnCompletionListener {

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp != null) {
            mp.release();
        }
    }
}