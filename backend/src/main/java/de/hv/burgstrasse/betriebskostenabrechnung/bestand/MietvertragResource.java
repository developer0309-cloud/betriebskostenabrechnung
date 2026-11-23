package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.hv.burgstrasse.betriebskostenabrechnung.api.ApiFehler.status;

@Path("/api/mietvertraege")
@RolesAllowed("eigentuemer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MietvertragResource {
    static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Inject
    Clock clock;

    @GET
    public List<Ansicht> list() {
        return Mietvertrag.<Mietvertrag>list("order by id").stream().map(Ansicht::von).toList();
    }

    @POST
    @Transactional
    public Ansicht anlegen(Eingabe eingabe) {
        Mieteinheit einheit = einheit(eingabe.mieteinheitId());
        Mieter mieter = mieter(eingabe.mieterId());
        Zeitraum zeitraum = zeitraum(eingabe);
        LocalDate heute = LocalDate.now(clock);
        String id = Vertragsregeln.identifikation(einheit.gebaeude.kuerzel, einheit.bezeichnung, heute);
        if (Mietvertrag.findById(id) != null) {
            throw status(409, "Für diese Mieteinheit gibt es heute schon einen Mietvertrag.");
        }
        pruefeUeberlappung(einheit, zeitraum, null);
        Mietvertrag vertrag = new Mietvertrag();
        vertrag.id = id;
        vertrag.mieteinheit = einheit;
        vertrag.mieter = mieter;
        vertrag.erstelltAm = heute;
        uebernehmen(vertrag, zeitraum, eingabe);
        vertrag.persist();
        return Ansicht.von(vertrag);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Ansicht aendern(@PathParam("id") String id, Eingabe eingabe) {
        Mietvertrag vertrag = finden(id);
        if (!vertrag.mieteinheit.id.equals(eingabe.mieteinheitId())) {
            throw status(400, "Die Mieteinheit eines Vertrags bleibt.");
        }
        Zeitraum zeitraum = zeitraum(eingabe);
        pruefeUeberlappung(vertrag.mieteinheit, zeitraum, vertrag.id);
        vertrag.mieter = mieter(eingabe.mieterId());
        uebernehmen(vertrag, zeitraum, eingabe);
        return Ansicht.von(vertrag);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void loeschen(@PathParam("id") String id) {
        finden(id).delete();
    }

    private static void pruefeUeberlappung(Mieteinheit einheit, Zeitraum zeitraum, String eigeneId) {
        List<Mietvertrag> vorhandene = Mietvertrag.list("mieteinheit", einheit);
        for (Mietvertrag anderer : vorhandene) {
            if (anderer.id.equals(eigeneId)) {
                continue;
            }
            if (Vertragsregeln.ueberschneidet(
                    zeitraum.beginn(), zeitraum.ende(), anderer.vertragsbeginn, anderer.vertragsende)) {
                throw status(409, "Die Vertragszeit überschneidet einen anderen Vertrag dieser Mieteinheit.");
            }
        }
    }

    private static Mietvertrag finden(String id) {
        Mietvertrag vertrag = Mietvertrag.findById(id);
        if (vertrag == null) {
            throw status(404, "Mietvertrag nicht gefunden.");
        }
        return vertrag;
    }

    private static Mieteinheit einheit(Long id) {
        if (id == null) {
            throw status(400, "Mieteinheit fehlt.");
        }
        Mieteinheit einheit = Mieteinheit.findById(id);
        if (einheit == null) {
            throw status(400, "Mieteinheit nicht gefunden.");
        }
        return einheit;
    }

    private static Mieter mieter(Long id) {
        if (id == null) {
            throw status(400, "Mieter fehlt.");
        }
        Mieter mieter = Mieter.findById(id);
        if (mieter == null) {
            throw status(400, "Mieter nicht gefunden.");
        }
        return mieter;
    }

    private static Zeitraum zeitraum(Eingabe eingabe) {
        if (eingabe.vertragsbeginn() == null || eingabe.vertragsende() == null) {
            throw status(400, "Vertragsbeginn und Vertragsende fehlen.");
        }
        if (eingabe.vertragsbeginn().isAfter(eingabe.vertragsende())) {
            throw status(400, "Der Vertragsbeginn liegt nach dem Vertragsende.");
        }
        return new Zeitraum(eingabe.vertragsbeginn(), eingabe.vertragsende());
    }

    private static void uebernehmen(Mietvertrag vertrag, Zeitraum zeitraum, Eingabe eingabe) {
        vertrag.vertragsbeginn = zeitraum.beginn();
        vertrag.vertragsende = zeitraum.ende();
        vorauszahlungenSetzen(vertrag, eingabe.vorauszahlungen());
    }

    private static void vorauszahlungenSetzen(Mietvertrag vertrag, List<VorauszahlungEingabe> eingaben) {
        List<VorauszahlungEingabe> zeilen = eingaben == null ? List.of() : eingaben;
        Map<Integer, BigDecimal> geprueft = new LinkedHashMap<>();
        for (VorauszahlungEingabe eingabe : zeilen) {
            if (eingabe == null || eingabe.jahr() == null) {
                throw status(400, "Jahr fehlt.");
            }
            if (geprueft.containsKey(eingabe.jahr())) {
                throw status(400, "Für das Jahr " + eingabe.jahr() + " ist schon eine Betriebskostenvorauszahlung erfasst.");
            }
            if (!Vertragsregeln.jahrSchneidetVertragszeit(eingabe.jahr(), vertrag.vertragsbeginn, vertrag.vertragsende)) {
                throw status(400, "Das Jahr " + eingabe.jahr() + " liegt außerhalb der Vertragszeit.");
            }
            geprueft.put(eingabe.jahr(), geld(eingabe.betrag(), "Betriebskostenvorauszahlung"));
        }
        vertrag.vorauszahlungen.removeIf(zeile -> !geprueft.containsKey(zeile.jahr));
        for (Map.Entry<Integer, BigDecimal> eintrag : geprueft.entrySet()) {
            Jahresvorauszahlung zeile = vertrag.vorauszahlungen.stream()
                    .filter(vorhanden -> eintrag.getKey().equals(vorhanden.jahr))
                    .findFirst()
                    .orElse(null);
            if (zeile == null) {
                zeile = new Jahresvorauszahlung();
                zeile.mietvertrag = vertrag;
                zeile.jahr = eintrag.getKey();
                vertrag.vorauszahlungen.add(zeile);
            }
            zeile.betrag = eintrag.getValue();
        }
    }

    private static BigDecimal geld(BigDecimal wert, String feld) {
        if (wert == null || wert.signum() < 0) {
            throw status(400, feld + " fehlt oder ist negativ.");
        }
        try {
            return wert.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw status(400, feld + " ist nicht auf den Cent genau.");
        }
    }

    public record VorauszahlungEingabe(Integer jahr, BigDecimal betrag) {
    }

    public record Eingabe(
            Long mieteinheitId,
            Long mieterId,
            LocalDate vertragsbeginn,
            LocalDate vertragsende,
            List<VorauszahlungEingabe> vorauszahlungen) {
    }

    public record Zeitraum(LocalDate beginn, LocalDate ende) {
    }

    public record VorauszahlungAnsicht(Integer jahr, BigDecimal betrag) {
    }

    public record Ansicht(
            String id,
            Long mieteinheitId,
            String kuerzel,
            String bezeichnung,
            Long mieterId,
            LocalDate vertragsbeginn,
            LocalDate vertragsende,
            List<VorauszahlungAnsicht> vorauszahlungen) {
        static Ansicht von(Mietvertrag vertrag) {
            List<VorauszahlungAnsicht> vorauszahlungen = vertrag.vorauszahlungen.stream()
                    .sorted(Comparator.comparing(zeile -> zeile.jahr))
                    .map(zeile -> new VorauszahlungAnsicht(zeile.jahr, zeile.betrag))
                    .toList();
            return new Ansicht(
                    vertrag.id,
                    vertrag.mieteinheit.id,
                    vertrag.mieteinheit.gebaeude.kuerzel,
                    vertrag.mieteinheit.bezeichnung,
                    vertrag.mieter.id,
                    vertrag.vertragsbeginn,
                    vertrag.vertragsende,
                    vorauszahlungen);
        }
    }
}
