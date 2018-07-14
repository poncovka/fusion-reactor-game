package cz.jmpionyr.pstp.fusionreactor.reactant;

import java.util.ArrayList;
import java.util.List;

public class Reactant {

    // List of all reactants
    private static final List<Reactant> all = new ArrayList<>();

    // Elements
    public static final Reactant VODA = new Reactant("VODA");
    public static final Reactant VZDUCH = new Reactant("VZDUCH");
    public static final Reactant OHEN = new Reactant("OHEN");
    public static final Reactant ZEME = new Reactant("ZEME");

    // Compounds
    public static final Reactant PARA = new Reactant("PARA", VODA, OHEN);
    public static final Reactant DEST = new Reactant("DEST", VODA, VZDUCH);
    public static final Reactant ENERGIE = new Reactant("ENERGIE", VZDUCH, OHEN);
    public static final Reactant LAVA = new Reactant("LAVA", OHEN, ZEME);

    // Properties
    private final String name;
    private final Reactant first_reactant;
    private final Reactant second_reactant;

    public Reactant(String name) {
        this(name, null, null);
    }

    public Reactant(String name, Reactant first_reactant, Reactant second_reactant) {
        // Initialize the reactant.
        this.name = name;
        this.first_reactant = first_reactant;
        this.second_reactant = second_reactant;

        // Add to the list of reactants.
        all.add(this);
    }

    public static List<Reactant> getAll() {
        return all;
    }

    public String getName() {
        return this.name;
    }

    public Reactant getFirstReactant() {
        return first_reactant;
    }

    public Reactant getSecondReactant() {
        return second_reactant;
    }

    public boolean isElement() {
        return this.first_reactant == null && this.second_reactant == null;
    }


}
