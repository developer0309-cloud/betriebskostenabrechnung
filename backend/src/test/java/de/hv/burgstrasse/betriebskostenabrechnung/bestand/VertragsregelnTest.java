package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class VertragsregelnTest {
    @Test
    void identifikationEnthaeltKuerzelEinheitUndTag() {
        LocalDate tag = LocalDate.of(2026, 11, 23);
        assertEquals("VH-EG-2026-11-23", Vertragsregeln.identifikation("VH", "EG", tag));
        assertEquals("HH-1. OG-2026-11-23", Vertragsregeln.identifikation("HH", "1. OG", tag));
    }

    @Test
    void vertragszeitenTeilenSichKeinenTag() {
        LocalDate januar = LocalDate.of(2026, 1, 1);
        LocalDate mitte = LocalDate.of(2026, 6, 30);
        LocalDate danach = LocalDate.of(2026, 7, 1);
        LocalDate ende = LocalDate.of(2026, 12, 31);
        assertTrue(Vertragsregeln.ueberschneidet(januar, mitte, mitte, ende));
        assertFalse(Vertragsregeln.ueberschneidet(januar, mitte, danach, ende));
    }

    @Test
    void vorauszahlungNurInJahrenDerVertragszeit() {
        LocalDate beginn = LocalDate.of(2026, 6, 1);
        LocalDate ende = LocalDate.of(2027, 3, 31);
        assertTrue(Vertragsregeln.jahrSchneidetVertragszeit(2026, beginn, ende));
        assertTrue(Vertragsregeln.jahrSchneidetVertragszeit(2027, beginn, ende));
        assertFalse(Vertragsregeln.jahrSchneidetVertragszeit(2025, beginn, ende));
        assertFalse(Vertragsregeln.jahrSchneidetVertragszeit(2028, beginn, ende));
    }
}
