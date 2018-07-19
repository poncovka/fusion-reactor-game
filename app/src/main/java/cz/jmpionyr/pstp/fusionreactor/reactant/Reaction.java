package cz.jmpionyr.pstp.fusionreactor.reactant;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Reaction {

    private static final String TAG = "Reaction";
    private static final Map<Set<String>, String> map = getReactions();

    public static String getProduct(String first, String second) {
        return map.get(getKey(first, second));
    }

    private static Map<Set<String>, String> getReactions() {
        Map<Set<String>, String> map = new HashMap<>();

        for (Reactant product : Reactant.getAll()) {

            if (!product.isElement()) {
                Reactant first = product.getFirstReactant();
                Reactant second = product.getSecondReactant();

                addReaction(map, first.getName(), second.getName(), product.getName());
            }
        }

        return map;
    }

    private static void addReaction(Map<Set<String>, String> map, String first, String second, String product) {
        Set<String> key = getKey(first, second);

        if (map.containsKey(key)) {
            Log.e(TAG, String.format("Same reaction for %s and %s", product, map.get(key)));
        }

        map.put(key, product);
    }

    private static Set<String> getKey(String first, String second) {
        Set<String> key = new HashSet<>(2);
        key.add(first);
        key.add(second);
        return key;
    }
}
