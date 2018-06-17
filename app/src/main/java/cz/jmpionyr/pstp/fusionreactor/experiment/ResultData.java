package cz.jmpionyr.pstp.fusionreactor.experiment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultData {

    private Map<Set<String>, String> map;

    public ResultData() {
        map = new HashMap<>();
        add("VZDUCH", "VZDUCH", "TLAK");
        add("OHEN", "OHEN", "SLUNCE");
        add("OHEN", "VZDUCH", "ENERGIE");
    }

    public String get(String first_reactant, String second_reactant) {
        return map.get(createKey(first_reactant, second_reactant));
    }

    private Set<String> createKey(String first_reactant, String second_reactant) {
        Set<String> key = new HashSet<>(2);
        key.add(first_reactant);
        key.add(second_reactant);
        return key;
    }

    private void add(String first_reactant, String second_reactant, String product) {
        map.put(createKey(first_reactant, second_reactant), product);
    }
}
