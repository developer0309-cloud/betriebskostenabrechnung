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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static de.hv.burgstrasse.betriebskostenabrechnung.api.ApiFehler.status;

@Path("/api/mieter")
@RolesAllowed("eigentuemer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MieterResource {
    @GET
    @Transactional
    public List<Ansicht> list() {
        return Mieter.<Mieter>list("order by id").stream().map(Ansicht::von).toList();
    }

    @POST
    @Transactional
    public Ansicht anlegen(Auswahl auswahl) {
        Mieter mieter = new Mieter();
        mieter.personen = personen(auswahl);
        mieter.persist();
        return Ansicht.von(mieter);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Ansicht aendern(@PathParam("id") Long id, Auswahl auswahl) {
        Mieter mieter = finden(id);
        mieter.personen = personen(auswahl);
        return Ansicht.von(mieter);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void loeschen(@PathParam("id") Long id) {
        Mieter mieter = finden(id);
        if (Mietvertrag.count("mieter", mieter) > 0) {
            throw status(409, "Der Mieter hat einen Mietvertrag.");
        }
        if (Beleg.count("mieter", mieter) > 0) {
            throw status(409, "Der Mieter hat einen Beleg.");
        }
        mieter.delete();
    }

    private static Mieter finden(Long id) {
        Mieter mieter = Mieter.findById(id);
        if (mieter == null) {
            throw status(404, "Mieter nicht gefunden.");
        }
        return mieter;
    }

    private static List<NatuerlichePerson> personen(Auswahl auswahl) {
        if (auswahl == null || auswahl.personIds() == null || auswahl.personIds().isEmpty()) {
            throw status(400, "Ein Mieter braucht mindestens eine natürliche Person.");
        }
        List<NatuerlichePerson> personen = new ArrayList<>();
        for (Long id : new LinkedHashSet<>(auswahl.personIds())) {
            NatuerlichePerson person = NatuerlichePerson.findById(id);
            if (person == null) {
                throw status(400, "Person nicht gefunden.");
            }
            personen.add(person);
        }
        return personen;
    }

    public record Auswahl(List<Long> personIds) {
    }

    public record Ansicht(Long id, List<PersonResource.Namen> personen, List<Long> personIds) {
        static Ansicht von(Mieter mieter) {
            List<PersonResource.Namen> namen = mieter.personen.stream()
                    .map(person -> new PersonResource.Namen(person.vorname, person.nachname))
                    .toList();
            List<Long> ids = mieter.personen.stream().map(person -> person.id).toList();
            return new Ansicht(mieter.id, namen, ids);
        }
    }
}
