package de.hv.burgstrasse.betriebskostenabrechnung.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static de.hv.burgstrasse.betriebskostenabrechnung.api.ApiFehler.status;

@Path("/api/auth")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
    @Inject
    @ConfigProperty(name = "app.auth.username")
    String benutzername;

    @Inject
    @ConfigProperty(name = "app.auth.password")
    String passwort;

    @POST
    @Path("/login")
    @PermitAll
    public Token login(Anmeldung anmeldung) {
        if (anmeldung == null
                || !benutzername.equals(anmeldung.benutzername())
                || !passwort.equals(anmeldung.passwort())) {
            throw status(401, "Benutzername oder Passwort ist falsch.");
        }
        String token = Jwt.issuer("betriebskostenabrechnung")
                .upn(benutzername)
                .groups(Set.of("eigentuemer"))
                .sign();
        return new Token(token);
    }

    public record Anmeldung(String benutzername, String passwort) {
    }

    public record Token(String token) {
    }
}
