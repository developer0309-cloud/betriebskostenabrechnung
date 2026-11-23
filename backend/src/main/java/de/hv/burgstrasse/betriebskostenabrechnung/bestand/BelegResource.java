package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import jakarta.annotation.security.RolesAllowed;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import static de.hv.burgstrasse.betriebskostenabrechnung.api.ApiFehler.status;

@Path("/api/belege")
@RolesAllowed("eigentuemer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Belege", description = "Kosten. Ein Beleg hat eine technische Identifikation, ein Valutadatum, einen Betrag in EUR, eine optionale Beschreibung, eine Kostenart und das Kennzeichen direkt oder indirekt. Direkte Kosten tragen die Kostenart Direkte Kosten und einen Mieter. Indirekte Kosten haben keinen Mieter.")
public class BelegResource {
    @GET
    @Transactional
    @Operation(summary = "Belege auflisten")
    public List<Ansicht> list() {
        return Beleg.<Beleg>list("order by id").stream().map(Ansicht::von).toList();
    }

    @GET
    @Path("/kostenarten")
    @Operation(summary = "Bekannte Kostenarten")
    public List<String> kostenarten() {
        return Arrays.stream(Kostenart.values()).map(Kostenart::bezeichnung).toList();
    }

    @POST
    @Transactional
    @Operation(summary = "Beleg anlegen")
    public Ansicht anlegen(Eingabe eingabe) {
        Beleg beleg = new Beleg();
        uebernehmen(beleg, eingabe);
        beleg.persist();
        return Ansicht.von(beleg);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Beleg ändern. Die Identifikation bleibt.")
    public Ansicht aendern(@PathParam("id") Long id, Eingabe eingabe) {
        Beleg beleg = finden(id);
        uebernehmen(beleg, eingabe);
        return Ansicht.von(beleg);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Beleg löschen")
    public void loeschen(@PathParam("id") Long id) {
        finden(id).delete();
    }

    private static Beleg finden(Long id) {
        Beleg beleg = Beleg.findById(id);
        if (beleg == null) {
            throw status(404, "Beleg nicht gefunden.");
        }
        return beleg;
    }

    private static void uebernehmen(Beleg beleg, Eingabe eingabe) {
        if (eingabe == null || eingabe.valutadatum() == null) {
            throw status(400, "Valutadatum fehlt.");
        }
        beleg.valutadatum = eingabe.valutadatum();
        beleg.betrag = geld(eingabe.betrag());
        beleg.beschreibung = beschreibung(eingabe.beschreibung());
        zuordnen(beleg, eingabe);
    }

    private static void zuordnen(Beleg beleg, Eingabe eingabe) {
        Kostenkennzeichen kennzeichen = Kostenkennzeichen.von(eingabe.kennzeichen())
                .orElseThrow(() -> status(400, "Kennzeichen fehlt oder ist unbekannt."));
        Kostenart art = kostenart(eingabe.kostenart());
        if (kennzeichen == Kostenkennzeichen.DIREKT) {
            if (art != Kostenart.DIREKTE_KOSTEN) {
                throw status(400, "Direkte Kosten haben die Kostenart Direkte Kosten.");
            }
            if (eingabe.mieterId() == null) {
                throw status(400, "Direkte Kosten brauchen einen Mieter.");
            }
            Mieter mieter = Mieter.findById(eingabe.mieterId());
            if (mieter == null) {
                throw status(400, "Mieter nicht gefunden.");
            }
            beleg.kennzeichen = kennzeichen;
            beleg.kostenart = art;
            beleg.mieter = mieter;
            return;
        }
        if (art == Kostenart.DIREKTE_KOSTEN) {
            throw status(400, "Indirekte Kosten haben nicht die Kostenart Direkte Kosten.");
        }
        if (eingabe.mieterId() != null) {
            throw status(400, "Indirekte Kosten haben keinen Mieter.");
        }
        beleg.kennzeichen = kennzeichen;
        beleg.kostenart = art;
        beleg.mieter = null;
    }

    private static BigDecimal geld(BigDecimal wert) {
        if (wert == null || wert.signum() < 0) {
            throw status(400, "Betrag fehlt oder ist negativ.");
        }
        try {
            return wert.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw status(400, "Betrag ist nicht auf den Cent genau.");
        }
    }

    private static String beschreibung(String wert) {
        if (wert == null || wert.isBlank()) {
            return null;
        }
        String text = wert.trim();
        if (text.length() > 500) {
            throw status(400, "Beschreibung ist zu lang.");
        }
        return text;
    }

    private static Kostenart kostenart(String bezeichnung) {
        return Kostenart.von(bezeichnung).orElseThrow(() -> status(400, "Kostenart fehlt oder ist unbekannt."));
    }

    @Schema(description = "Eingabe eines Belegs. Die Identifikation vergibt die Anlage.")
    public record Eingabe(
            @Schema(description = "Valutadatum", required = true) LocalDate valutadatum,
            @Schema(description = "Betrag in EUR, auf den Cent genau, größer oder gleich null", required = true) BigDecimal betrag,
            @Schema(description = "Optionale Beschreibung") String beschreibung,
            @Schema(description = "Kostenart. Bei direkten Kosten genau Direkte Kosten.", required = true, enumeration = {
                    "Grundsteuer",
                    "Wartung Rauchwarnmelder",
                    "Allgemeinstrom",
                    "Hausreinigung",
                    "Gebäudeversicherung",
                    "Glasversicherung",
                    "Strassenreinigung",
                    "Entwässerung",
                    "Müllentsorgung",
                    "Direkte Kosten"
            }) String kostenart,
            @Schema(description = "Kennzeichen direkt oder indirekt", required = true, enumeration = {"direkt", "indirekt"}) String kennzeichen,
            @Schema(description = "Mieter. Obligate bei direkten Kosten, sonst leer.") Long mieterId) {
    }

    @Schema(description = "Beleg der Kosten")
    public record Ansicht(
            @Schema(description = "Technische Identifikation") Long id,
            LocalDate valutadatum,
            BigDecimal betrag,
            String beschreibung,
            String kostenart,
            String kennzeichen,
            Long mieterId,
            @Schema(description = "Namen der natürlichen Personen des Mieters. Fehlt bei indirekten Kosten.") String mieterName) {
        static Ansicht von(Beleg beleg) {
            return new Ansicht(
                    beleg.id,
                    beleg.valutadatum,
                    beleg.betrag,
                    beleg.beschreibung,
                    beleg.kostenart.bezeichnung(),
                    beleg.kennzeichen.bezeichnung(),
                    beleg.mieter == null ? null : beleg.mieter.id,
                    mieterName(beleg.mieter));
        }

        private static String mieterName(Mieter mieter) {
            if (mieter == null) {
                return null;
            }
            return mieter.personen.stream()
                    .map(person -> person.vorname + " " + person.nachname)
                    .collect(Collectors.joining(", "));
        }
    }
}
