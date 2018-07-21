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
    public static final Reactant ENERGIE = new Reactant("ENERGIE", VZDUCH, OHEN);
    public static final Reactant PARA = new Reactant("PARA", VODA, OHEN);
    public static final Reactant DEST = new Reactant("DEST", VODA, VZDUCH);
    public static final Reactant LAVA = new Reactant("LAVA", OHEN, ZEME);
    public static final Reactant BAHNO = new Reactant("BAHNO", VODA, ZEME);
    public static final Reactant OBSIDIAN = new Reactant("OBSIDIAN", VODA, LAVA);
    public static final Reactant PRACH = new Reactant("PRACH", VZDUCH, LAVA);
    public static final Reactant KAMEN = new Reactant("KAMEN", VZDUCH, LAVA);
    public static final Reactant ROSTLINA = new Reactant("ROSTLINA", DEST, ZEME);
    public static final Reactant BAZINA = new Reactant("BAZINA", ROSTLINA, BAHNO);
    public static final Reactant STRELNY_PRACH = new Reactant("STRELNY_PRACH", PRACH, OHEN);
    public static final Reactant KOV = new Reactant("KOV", KAMEN, OHEN);
    public static final Reactant ZIVOT = new Reactant("ZIVOT", ENERGIE, BAZINA);
    public static final Reactant CLOVEK = new Reactant("CLOVEK", ZIVOT, ZEME);
    public static final Reactant SPERK = new Reactant("SPERK", OBSIDIAN, KOV);
    public static final Reactant PISEK = new Reactant("PISEK", VZDUCH, KAMEN);
    public static final Reactant ELEKTRINA = new Reactant("ELEKTRINA", ENERGIE, KOV);
    public static final Reactant NARADI = new Reactant("NARADI", KOV, KOV);
    public static final Reactant ROBOT = new Reactant("ROBOT", KOV, ZIVOT);
    public static final Reactant KERAMICKA_HLINA = new Reactant("KERAMICKA_HLINA", PISEK, BAHNO);
    public static final Reactant KERAMIKA = new Reactant("KERAMIKA", OHEN, KERAMICKA_HLINA);
    public static final Reactant KOTEL = new Reactant("KOTEL", PARA, KOV);
    public static final Reactant SKLO = new Reactant("SKLO", PISEK, OHEN);
    public static final Reactant ELEKTRICKY_DRAT = new Reactant("ELEKTRICKY_DRAT", ELEKTRINA, KOV);
    public static final Reactant DRON = new Reactant("DRON", VZDUCH, ROBOT);
    public static final Reactant BIOMECHATRONIKA = new Reactant("BIOMECHATRONIKA", ROBOT, ZIVOT);
    public static final Reactant BAKTERIE = new Reactant("BAKTERIE", ZIVOT, BAZINA);
    public static final Reactant LEK = new Reactant("LEK", ROBOT, BAKTERIE);
    public static final Reactant LUPA = new Reactant("LUPA", SKLO, SKLO);
    public static final Reactant ZAROVKA = new Reactant("ZAROVKA", SKLO, ELEKTRINA);
    public static final Reactant LOKOMOTIVA = new Reactant("LOKOMOTIVA", KOTEL, NARADI);
    public static final Reactant SVETLO = new Reactant("SVETLO", ZAROVKA, ELEKTRICKY_DRAT);
    public static final Reactant DUHA = new Reactant("DUHA", DEST, SVETLO);
    public static final Reactant MIKROSKOP = new Reactant("MIKROSKOP", LUPA, ELEKTRINA);
    public static final Reactant POCITAC = new Reactant("POCITAC", ELEKTRICKY_DRAT, NARADI);
    public static final Reactant OPTICKE_VLAKNO = new Reactant("OPTICKE_VLAKNO", SVETLO, ELEKTRICKY_DRAT);
    public static final Reactant INTERNET = new Reactant("INTERNET", OPTICKE_VLAKNO, POCITAC);

    // Properties
    private final String name;
    private final Reactant first_reactant;
    private final Reactant second_reactant;

    private Reactant(String name) {
        this(name, null, null);
    }

    private Reactant(String name, Reactant first_reactant, Reactant second_reactant) {
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
