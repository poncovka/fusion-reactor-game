package cz.jmpionyr.pstp.fusionreactor.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.jmpionyr.pstp.fusionreactor.R;

class ExperimentSound {

    private static final Random random = new Random();

    public static int getRandomProgressMessage() {
        return getRandomMessage(getProgressMessages());
    }

    public static int getRandomErrorMessage() {
        return getRandomMessage(getErrorMessages());
    }

    public static int getRandomSuccessfulMessage() {
        return getRandomMessage(getSuccessfulMessages());
    }

    private static int getRandomMessage(List<Integer> messages) {
        return messages.get(random.nextInt(messages.size()));
    }

    private static List<Integer> getProgressMessages() {
        List<Integer> list = new ArrayList<>();
        list.add(R.raw.progress_msg_01);
        list.add(R.raw.progress_msg_02);
        list.add(R.raw.progress_msg_03);
        list.add(R.raw.progress_msg_04);
        list.add(R.raw.progress_msg_05);
        list.add(R.raw.progress_msg_06);
        list.add(R.raw.progress_msg_07);
        list.add(R.raw.progress_msg_08);
        list.add(R.raw.progress_msg_09);
        list.add(R.raw.progress_msg_10);
        list.add(R.raw.progress_msg_11);
        list.add(R.raw.progress_msg_12);
        list.add(R.raw.progress_msg_13);
        list.add(R.raw.progress_msg_14);
        list.add(R.raw.progress_msg_15);
        list.add(R.raw.progress_msg_16);
        list.add(R.raw.progress_msg_17);
        list.add(R.raw.progress_msg_18);
        list.add(R.raw.progress_msg_19);
        list.add(R.raw.progress_msg_20);
        list.add(R.raw.progress_msg_21);
        list.add(R.raw.progress_msg_22);
        list.add(R.raw.progress_msg_23);
        list.add(R.raw.progress_msg_24);
        list.add(R.raw.progress_msg_25);
        return list;
    }


    private static List<Integer> getErrorMessages() {
        List<Integer> list = new ArrayList<>();
        list.add(R.raw.error_msg_01);
        //list.add(R.raw.error_msg_02);
        list.add(R.raw.error_msg_03);
        list.add(R.raw.error_msg_04);
        list.add(R.raw.error_msg_05);
        list.add(R.raw.error_msg_06);
        list.add(R.raw.error_msg_07);
        list.add(R.raw.error_msg_08);
        list.add(R.raw.error_msg_09);
        list.add(R.raw.error_msg_10);
        list.add(R.raw.error_msg_11);
        list.add(R.raw.error_msg_12);
        list.add(R.raw.error_msg_13);
        list.add(R.raw.error_msg_14);
        list.add(R.raw.error_msg_15);
        return list;
    }

    private static List<Integer> getSuccessfulMessages() {
        List<Integer> list = new ArrayList<>();
        list.add(R.raw.success_msg_01);
        list.add(R.raw.success_msg_02);
        list.add(R.raw.success_msg_03);
        list.add(R.raw.success_msg_04);
        list.add(R.raw.success_msg_05);
        list.add(R.raw.success_msg_06);
        list.add(R.raw.success_msg_07);
        list.add(R.raw.success_msg_08);
        list.add(R.raw.success_msg_09);
        list.add(R.raw.success_msg_10);
        //list.add(R.raw.success_msg_11);
        list.add(R.raw.success_msg_12);
        list.add(R.raw.success_msg_13);
        list.add(R.raw.success_msg_14);
        list.add(R.raw.success_msg_15);
        return list;
    }

}
