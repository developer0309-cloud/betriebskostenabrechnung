package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import java.util.Arrays;
import java.util.Optional;

public enum Kostenart {
    GRUNDSTEUER("Grundsteuer"),
    WARTUNG_RAUCHWARNMELDER("Wartung Rauchwarnmelder"),
    ALLGEMEINSTROM("Allgemeinstrom"),
    HAUSREINIGUNG("Hausreinigung"),
    GEBAEUDEVERSICHERUNG("Gebäudeversicherung"),
    GLASVERSICHERUNG("Glasversicherung"),
    STRASSENREINIGUNG("Strassenreinigung"),
    ENTWAESSERUNG("Entwässerung"),
    MUELLENTSORGUNG("Müllentsorgung"),
    DIREKTE_KOSTEN("Direkte Kosten");

    private final String bezeichnung;

    Kostenart(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String bezeichnung() {
        return bezeichnung;
    }

    public static Optional<Kostenart> von(String bezeichnung) {
        if (bezeichnung == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(art -> art.bezeichnung.equals(bezeichnung.trim())).findFirst();
    }
}
