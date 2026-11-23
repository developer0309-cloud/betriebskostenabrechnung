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
import java.util.List;

import static de.hv.burgstrasse.betriebskostenabrechnung.api.ApiFehler.status;

@Path("/api/personen")
@RolesAllowed("eigentuemer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
    @GET
    public List<NatuerlichePerson> list() {
        return NatuerlichePerson.list("order by nachname, vorname, id");
    }

    @POST
    @Transactional
    public NatuerlichePerson anlegen(Namen namen) {
        NatuerlichePerson person = new NatuerlichePerson();
        uebernehmen(person, namen);
        person.persist();
        return person;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public NatuerlichePerson aendern(@PathParam("id") Long id, Namen namen) {
        NatuerlichePerson person = finden(id);
        uebernehmen(person, namen);
        return person;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void loeschen(@PathParam("id") Long id) {
        NatuerlichePerson person = finden(id);
        long verwendet = Mieter.find("select m from Mieter m join m.personen p where p = ?1", person).count();
        if (verwendet > 0) {
            throw status(409, "Die Person ist einem Mieter zugeordnet.");
        }
        person.delete();
    }

    private static NatuerlichePerson finden(Long id) {
        NatuerlichePerson person = NatuerlichePerson.findById(id);
        if (person == null) {
            throw status(404, "Person nicht gefunden.");
        }
        return person;
    }

    private static void uebernehmen(NatuerlichePerson person, Namen namen) {
        if (namen == null) {
            throw status(400, "Vorname und Nachname fehlen.");
        }
        person.vorname = text(namen.vorname(), "Vorname");
        person.nachname = text(namen.nachname(), "Nachname");
    }

    static String text(String wert, String feld) {
        if (wert == null || wert.isBlank()) {
            throw status(400, feld + " fehlt.");
        }
        return wert.trim();
    }

    public record Namen(String vorname, String nachname) {
    }
}
