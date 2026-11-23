package de.hv.burgstrasse.betriebskostenabrechnung.api;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public final class ApiFehler {
    private ApiFehler() {
    }

    public static WebApplicationException status(int status, String meldung) {
        return new WebApplicationException(Response.status(status)
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .entity(new Meldung(meldung))
                .build());
    }

    public record Meldung(String meldung) {
    }
}
