package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import java.util.Arrays;
import java.util.Optional;

public enum Kostenkennzeichen {
    DIREKT("direkt"),
    INDIREKT("indirekt");

    private final String bezeichnung;

    Kostenkennzeichen(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String bezeichnung() {
        return bezeichnung;
    }

    public static Optional<Kostenkennzeichen> von(String bezeichnung) {
        if (bezeichnung == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(wert -> wert.bezeichnung.equals(bezeichnung.trim())).findFirst();
    }
}
