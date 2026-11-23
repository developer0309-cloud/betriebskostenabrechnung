package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import java.time.LocalDate;

public final class Vertragsregeln {
    private Vertragsregeln() {
    }

    public static String identifikation(String gebaeudekuerzel, String mieteinheit, LocalDate erstelldatum) {
        return gebaeudekuerzel + "-" + mieteinheit + "-" + erstelldatum;
    }

    public static boolean ueberschneidet(LocalDate beginn, LocalDate ende, LocalDate andererBeginn, LocalDate anderesEnde) {
        return !beginn.isAfter(anderesEnde) && !andererBeginn.isAfter(ende);
    }

    public static boolean jahrSchneidetVertragszeit(int jahr, LocalDate beginn, LocalDate ende) {
        if (jahr < 1 || jahr > 9999) {
            return false;
        }
        return ueberschneidet(LocalDate.of(jahr, 1, 1), LocalDate.of(jahr, 12, 31), beginn, ende);
    }
}
